package com.pet.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.management.entity.HealthRecord;
import com.pet.management.mapper.HealthRecordMapper;
import com.pet.management.service.HealthRecordService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {
    @Override
    public List<HealthRecord> getHealthRecordsByPetId(Long petId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getPetId, petId)
                .orderByDesc(HealthRecord::getRecordDate)
        );
    }

    @Override
    public List<HealthRecord> getHealthRecordsByPetIdAndType(Long petId, String type) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getPetId, petId)
                .eq(HealthRecord::getType, type)
                .orderByDesc(HealthRecord::getRecordDate)
        );
    }

    @Override
    public Workbook generateHealthReport(Long petId, Date startDate, Date endDate) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("健康记录报表");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("记录ID");
        headerRow.createCell(1).setCellValue("记录类型");
        headerRow.createCell(2).setCellValue("内容");
        headerRow.createCell(3).setCellValue("记录日期");
        headerRow.createCell(4).setCellValue("医生");
        headerRow.createCell(5).setCellValue("备注");

        // 查询健康记录
        List<HealthRecord> records = baseMapper.selectList(
            new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getPetId, petId)
                .ge(HealthRecord::getRecordDate, startDate)
                .le(HealthRecord::getRecordDate, endDate)
                .orderByDesc(HealthRecord::getRecordDate)
        );

        // 填充数据
        for (int i = 0; i < records.size(); i++) {
            HealthRecord record = records.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(record.getId());
            row.createCell(1).setCellValue(record.getType());
            row.createCell(2).setCellValue(record.getContent());
            row.createCell(3).setCellValue(record.getRecordDate().toString());
            row.createCell(4).setCellValue(record.getDoctor());
            row.createCell(5).setCellValue(record.getNotes());
        }

        // 自动调整列宽
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }
}