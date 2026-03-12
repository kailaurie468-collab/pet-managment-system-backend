package com.pet.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.management.entity.HealthRecord;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Date;

public interface HealthRecordService extends IService<HealthRecord> {
    List<HealthRecord> getHealthRecordsByPetId(Long petId);
    List<HealthRecord> getHealthRecordsByPetIdAndType(Long petId, String type);
    Workbook generateHealthReport(Long petId, Date startDate, Date endDate);
}