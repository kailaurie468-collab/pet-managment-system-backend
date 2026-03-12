package com.pet.management.service;

import org.apache.poi.ss.usermodel.Workbook;
import com.pet.management.entity.ServiceRecord;

import java.util.List;
import java.util.Date;

public interface ExpenseService {
    List<ServiceRecord> generateExpenseList(Long petId, Date startDate, Date endDate);
    Double calculateTotalExpense(Long petId, Date startDate, Date endDate);
    Workbook exportExpenseExcel(Long petId, Date startDate, Date endDate);
}