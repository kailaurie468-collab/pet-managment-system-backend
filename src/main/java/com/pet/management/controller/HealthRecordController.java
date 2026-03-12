package com.pet.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.management.entity.HealthRecord;
import com.pet.management.entity.Pet;
import com.pet.management.entity.User;
import com.pet.management.mapper.UserMapper;
import com.pet.management.service.HealthRecordService;
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
@RequestMapping("/api/health-records")
public class HealthRecordController {
    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PetService petService;

    /**
     * 获取当前用户拥有的宠物 ID 列表（普通用户使用）
     */
    private List<Long> getCurrentUserPetIds() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (currentUser == null)
            return Collections.emptyList();
        return petService.getPetsByOwnerId(currentUser.getId())
                .stream().map(Pet::getId).collect(Collectors.toList());
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping
    public HealthRecord createHealthRecord(@RequestBody HealthRecord healthRecord) {
        healthRecordService.save(healthRecord);
        return healthRecord;
    }

    @GetMapping
    public List<HealthRecord> getAllHealthRecords() {
        if (isAdmin()) {
            return healthRecordService.list();
        }
        List<Long> petIds = getCurrentUserPetIds();
        if (petIds.isEmpty())
            return Collections.emptyList();
        return healthRecordService.list(new LambdaQueryWrapper<HealthRecord>().in(HealthRecord::getPetId, petIds));
    }

    @GetMapping("/pet/{petId}")
    public List<HealthRecord> getHealthRecordsByPetId(@PathVariable Long petId) {
        return healthRecordService.getHealthRecordsByPetId(petId);
    }

    @GetMapping("/pet/{petId}/type/{type}")
    public List<HealthRecord> getHealthRecordsByPetIdAndType(@PathVariable Long petId, @PathVariable String type) {
        return healthRecordService.getHealthRecordsByPetIdAndType(petId, type);
    }

    @GetMapping("/{id}")
    public HealthRecord getHealthRecordById(@PathVariable Long id) {
        return healthRecordService.getById(id);
    }

    @PutMapping("/{id}")
    public HealthRecord updateHealthRecord(@PathVariable Long id, @RequestBody HealthRecord healthRecord) {
        healthRecord.setId(id);
        healthRecordService.updateById(healthRecord);
        return healthRecord;
    }

    @DeleteMapping("/{id}")
    public void deleteHealthRecord(@PathVariable Long id) {
        healthRecordService.removeById(id);
    }

    @GetMapping("/report/{petId}")
    public void generateHealthReport(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            HttpServletResponse response) throws IOException {
        Workbook workbook = healthRecordService.generateHealthReport(petId, startDate, endDate);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=health_report.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}