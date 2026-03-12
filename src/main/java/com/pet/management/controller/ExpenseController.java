package com.pet.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.management.entity.Pet;
import com.pet.management.entity.ServiceRecord;
import com.pet.management.entity.User;
import com.pet.management.mapper.UserMapper;
import com.pet.management.service.ExpenseService;
import com.pet.management.service.PetService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PetService petService;

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private List<Long> getCurrentUserPetIds() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (currentUser == null)
            return Collections.emptyList();
        return petService.getPetsByOwnerId(currentUser.getId())
                .stream().map(Pet::getId).collect(Collectors.toList());
    }

    /**
     * 普通用户只能查自己宠物的费用，管理员可查任意
     */
    private boolean canAccessPet(Long petId) {
        if (isAdmin())
            return true;
        return getCurrentUserPetIds().contains(petId);
    }

    @GetMapping("/list/{petId}")
    public List<ServiceRecord> generateExpenseList(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        if (!canAccessPet(petId))
            return Collections.emptyList();
        return expenseService.generateExpenseList(petId, startDate, endDate);
    }

    @GetMapping("/total/{petId}")
    public Double calculateTotalExpense(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        if (!canAccessPet(petId))
            return 0.0;
        return expenseService.calculateTotalExpense(petId, startDate, endDate);
    }

    @GetMapping("/export/{petId}")
    public void exportExpenseExcel(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            HttpServletResponse response) throws IOException {
        if (!canAccessPet(petId)) {
            response.setStatus(403);
            return;
        }
        Workbook workbook = expenseService.exportExpenseExcel(petId, startDate, endDate);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=expense_list.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}