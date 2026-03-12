package com.pet.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.management.entity.Pet;
import com.pet.management.entity.ServiceRecord;
import com.pet.management.entity.User;
import com.pet.management.mapper.UserMapper;
import com.pet.management.service.PetService;
import com.pet.management.service.ServiceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-records")
public class ServiceRecordController {
    @Autowired
    private ServiceRecordService serviceRecordService;

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

    @PostMapping
    public ServiceRecord createServiceRecord(@RequestBody ServiceRecord serviceRecord) {
        serviceRecordService.save(serviceRecord);
        return serviceRecord;
    }

    @GetMapping
    public List<ServiceRecord> getAllServiceRecords() {
        if (isAdmin()) {
            return serviceRecordService.list();
        }
        List<Long> petIds = getCurrentUserPetIds();
        if (petIds.isEmpty())
            return Collections.emptyList();
        return serviceRecordService.list(
                new LambdaQueryWrapper<ServiceRecord>().in(ServiceRecord::getPetId, petIds));
    }

    @GetMapping("/pet/{petId}")
    public List<ServiceRecord> getServiceRecordsByPetId(@PathVariable Long petId) {
        return serviceRecordService.getServiceRecordsByPetId(petId);
    }

    @GetMapping("/date-range")
    public List<ServiceRecord> getServiceRecordsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return serviceRecordService.getServiceRecordsByDateRange(startDate, endDate);
    }

    @GetMapping("/total/{petId}")
    public Double calculateTotalAmountByPetId(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return serviceRecordService.calculateTotalAmountByPetId(petId, startDate, endDate);
    }

    @GetMapping("/{id}")
    public ServiceRecord getServiceRecordById(@PathVariable Long id) {
        return serviceRecordService.getById(id);
    }

    @PutMapping("/{id}")
    public ServiceRecord updateServiceRecord(@PathVariable Long id, @RequestBody ServiceRecord serviceRecord) {
        serviceRecord.setId(id);
        serviceRecordService.updateById(serviceRecord);
        return serviceRecord;
    }

    @DeleteMapping("/{id}")
    public void deleteServiceRecord(@PathVariable Long id) {
        serviceRecordService.removeById(id);
    }
}