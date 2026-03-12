package com.pet.management.service.impl;

import com.pet.management.entity.ServiceRecord;
import com.pet.management.service.ExpenseService;
import com.pet.management.service.ServiceRecordService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ServiceRecordService serviceRecordService;

    @Override
    public List<ServiceRecord> generateExpenseList(Long petId, Date startDate, Date endDate) {
        return serviceRecordService.getServiceRecordsByPetId(petId).stream()
                .filter(record -> {
                    Date serviceDate = record.getServiceDate();
                    return serviceDate.after(startDate) && serviceDate.before(endDate);
                })
                .toList();
    }

    @Override
    public Double calculateTotalExpense(Long petId, Date startDate, Date endDate) {
        return serviceRecordService.calculateTotalAmountByPetId(petId, startDate, endDate);
    }

    @Override
    public Workbook exportExpenseExcel(Long petId, Date startDate, Date endDate) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("消费清单");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("记录ID");
        headerRow.createCell(1).setCellValue("服务日期");
        headerRow.createCell(2).setCellValue("服务项目ID");
        headerRow.createCell(3).setCellValue("金额");
        headerRow.createCell(4).setCellValue("备注");

        // 获取消费记录
        List<ServiceRecord> records = generateExpenseList(petId, startDate, endDate);

        // 填充数据
        for (int i = 0; i < records.size(); i++) {
            ServiceRecord record = records.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(record.getId());
            row.createCell(1).setCellValue(record.getServiceDate().toString());
            row.createCell(2).setCellValue(record.getServiceId());
            row.createCell(3).setCellValue(record.getAmount());
            row.createCell(4).setCellValue(record.getNotes());
        }

        // 添加总计行
        Row totalRow = sheet.createRow(records.size() + 1);
        totalRow.createCell(0).setCellValue("总计");
        totalRow.createCell(3).setCellValue(calculateTotalExpense(petId, startDate, endDate));

        // 自动调整列宽
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }
}