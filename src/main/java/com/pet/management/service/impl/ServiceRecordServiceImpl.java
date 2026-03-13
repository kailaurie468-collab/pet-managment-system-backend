package com.pet.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.management.entity.Doctor;
import com.pet.management.entity.HealthRecord;
import com.pet.management.entity.Service;
import com.pet.management.entity.ServiceRecord;
import com.pet.management.entity.VaccineRecord;
import com.pet.management.mapper.ServiceRecordMapper;
import com.pet.management.service.DoctorService;
import com.pet.management.service.HealthRecordService;
import com.pet.management.service.ServiceRecordService;
import com.pet.management.service.ServiceService;
import com.pet.management.service.VaccineRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceRecordServiceImpl extends ServiceImpl<ServiceRecordMapper, ServiceRecord>
        implements ServiceRecordService {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private VaccineRecordService vaccineRecordService;

    @Override
    public boolean save(ServiceRecord entity) {
        // 自动指派医生逻辑
        if (entity.getDoctorId() == null) {
            List<Doctor> doctors = doctorService.list();
            if (doctors != null && !doctors.isEmpty()) {
                Doctor randomDoctor = doctors.get(new Random().nextInt(doctors.size()));
                entity.setDoctorId(randomDoctor.getId());
            }
        }

        boolean saved = super.save(entity);

        // 联动逻辑：处理多项服务
        if (saved && entity.getServiceId() != null) {
            String[] serviceIds = entity.getServiceId().split(",");
            for (String sIdStr : serviceIds) {
                try {
                    Long sId = Long.parseLong(sIdStr.trim());
                    Service currentService = serviceService.getById(sId);
                    if (currentService == null) continue;

                    // 1. “医疗”检查联动逻辑 (健康记录)
                    if ("医疗".equals(currentService.getCategory())) {
                        HealthRecord hr = new HealthRecord();
                        hr.setPetId(entity.getPetId());
                        hr.setType("医疗");
                        hr.setContent("执行医疗服务: " + currentService.getName());
                        hr.setRecordDate(entity.getServiceDate() != null ? entity.getServiceDate() : new Date());
                        hr.setNotes(entity.getNotes());

                        if (entity.getDoctorId() != null) {
                            Doctor doc = doctorService.getById(entity.getDoctorId());
                            if (doc != null) {
                                hr.setDoctor(doc.getName());
                            }
                        }
                        healthRecordService.save(hr);
                    }

                    // 2. “疫苗”识别联动逻辑 (疫苗记录)
                    if (currentService.getName() != null && currentService.getName().contains("疫苗")) {
                        VaccineRecord vr = new VaccineRecord();
                        vr.setPetId(entity.getPetId());
                        vr.setVaccineName(currentService.getName());
                        vr.setVaccineDate(entity.getServiceDate() != null ? entity.getServiceDate() : new Date());
                        
                        // 预设计算：如果是年次接种（如含有“狂犬”或“疫苗”通用），默认一年后复种
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.setTime(vr.getVaccineDate());
                        cal.add(java.util.Calendar.YEAR, 1);
                        vr.setNextDueDate(cal.getTime());
                        
                        vr.setNotes("通过服务登记自动同步: " + entity.getNotes());
                        vaccineRecordService.save(vr);
                    }
                } catch (NumberFormatException e) {
                    log.error("解析服务ID失败: {}", sIdStr);
                }
            }
        }
        return saved;
    }

    @Override
    public List<ServiceRecord> getServiceRecordsByPetId(Long petId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<ServiceRecord>()
                        .eq(ServiceRecord::getPetId, petId)
                        .orderByDesc(ServiceRecord::getServiceDate));
    }

    @Override
    public List<ServiceRecord> getServiceRecordsByDateRange(Date startDate, Date endDate) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<ServiceRecord>()
                        .ge(ServiceRecord::getServiceDate, startDate)
                        .le(ServiceRecord::getServiceDate, endDate)
                        .orderByDesc(ServiceRecord::getServiceDate));
    }

    @Override
    public Double calculateTotalAmountByPetId(Long petId, Date startDate, Date endDate) {
        List<ServiceRecord> records = baseMapper.selectList(
                new LambdaQueryWrapper<ServiceRecord>()
                        .eq(ServiceRecord::getPetId, petId)
                        .ge(ServiceRecord::getServiceDate, startDate)
                        .le(ServiceRecord::getServiceDate, endDate));
        return records.stream().mapToDouble(ServiceRecord::getAmount).sum();
    }
}