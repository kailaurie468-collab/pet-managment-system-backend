package com.pet.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.management.entity.ServiceRecord;
import java.util.List;
import java.util.Date;

public interface ServiceRecordService extends IService<ServiceRecord> {
    List<ServiceRecord> getServiceRecordsByPetId(Long petId);
    List<ServiceRecord> getServiceRecordsByDateRange(Date startDate, Date endDate);
    Double calculateTotalAmountByPetId(Long petId, Date startDate, Date endDate);
}