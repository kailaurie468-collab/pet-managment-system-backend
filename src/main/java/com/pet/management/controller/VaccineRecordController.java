package com.pet.management.controller;

import com.pet.management.entity.VaccineRecord;
import com.pet.management.entity.Pet;
import com.pet.management.service.VaccineRecordService;
import com.pet.management.service.PetService;
import com.pet.management.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vaccine-records")
public class VaccineRecordController {

    @Autowired
    private VaccineRecordService vaccineRecordService;

    @Autowired
    private PetService petService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public List<VaccineRecord> getAllVaccineRecords() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.pet.management.entity.User user = userMapper.selectByUsername(username);

        if ("ADMIN".equals(user.getRole())) {
            return vaccineRecordService.list();
        } else {
            List<Long> petIds = petService.getPetsByOwnerId(user.getId())
                    .stream()
                    .map(Pet::getId)
                    .collect(Collectors.toList());
            if (petIds.isEmpty()) return List.of();
            return vaccineRecordService.lambdaQuery().in(VaccineRecord::getPetId, petIds).list();
        }
    }

    @PostMapping
    public boolean saveVaccineRecord(@RequestBody VaccineRecord record) {
        return vaccineRecordService.save(record);
    }

    @PutMapping("/{id}")
    public boolean updateVaccineRecord(@PathVariable Long id, @RequestBody VaccineRecord record) {
        record.setId(id);
        return vaccineRecordService.updateById(record);
    }

    @DeleteMapping("/{id}")
    public boolean deleteVaccineRecord(@PathVariable Long id) {
        return vaccineRecordService.removeById(id);
    }
}
