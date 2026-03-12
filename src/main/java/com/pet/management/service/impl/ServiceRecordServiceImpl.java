package com.pet.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.management.entity.Doctor;
import com.pet.management.entity.HealthRecord;
import com.pet.management.entity.Service;
import com.pet.management.entity.ServiceRecord;
import com.pet.management.mapper.ServiceRecordMapper;
import com.pet.management.service.DoctorService;
import com.pet.management.service.HealthRecordService;
import com.pet.management.service.ServiceRecordService;
import com.pet.management.service.ServiceService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Random;

@org.springframework.stereotype.Service
public class ServiceRecordServiceImpl extends ServiceImpl<ServiceRecordMapper, ServiceRecord>
        implements ServiceRecordService {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private HealthRecordService healthRecordService;

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

        // “医疗”检查联动逻辑
        if (saved && entity.getServiceId() != null) {
            Service currentService = serviceService.getById(entity.getServiceId());
            if (currentService != null && "医疗".equals(currentService.getCategory())) {
                HealthRecord hr = new HealthRecord();
                hr.setPetId(entity.getPetId());
                hr.setType("treatment"); // 根据业务固定为 treatment，或者复用检查类型
                hr.setContent("执行医疗服务: " + currentService.getName());
                hr.setRecordDate(entity.getServiceDate() != null ? entity.getServiceDate() : new Date());
                hr.setNotes(entity.getNotes());

                // 设置医生名字
                if (entity.getDoctorId() != null) {
                    Doctor doc = doctorService.getById(entity.getDoctorId());
                    if (doc != null) {
                        hr.setDoctor(doc.getName());
                    }
                }
                healthRecordService.save(hr);
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