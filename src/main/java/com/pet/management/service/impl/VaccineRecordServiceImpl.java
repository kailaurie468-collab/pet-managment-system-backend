package com.pet.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.management.entity.VaccineRecord;
import com.pet.management.mapper.VaccineRecordMapper;
import com.pet.management.service.VaccineRecordService;
import org.springframework.stereotype.Service;

@Service
public class VaccineRecordServiceImpl extends ServiceImpl<VaccineRecordMapper, VaccineRecord> implements VaccineRecordService {
}
