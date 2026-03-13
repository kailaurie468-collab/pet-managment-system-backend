package com.pet.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.management.entity.Pet;
import com.pet.management.entity.User;
import com.pet.management.mapper.UserMapper;
import com.pet.management.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    public Pet createPet(@RequestBody Pet pet) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (currentUser != null) {
            pet.setOwnerId(currentUser.getId());
        }
        petService.save(pet);
        return pet;
    }

    @GetMapping
    public List<Pet> getAllPets(
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) String ownerName) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        LambdaQueryWrapper<Pet> queryWrapper = new LambdaQueryWrapper<>();
        
        // 1. 处理主人名搜索 (针对管理员或特定逻辑)
        if (ownerName != null && !ownerName.isEmpty()) {
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().like(User::getName, ownerName));
            if (!users.isEmpty()) {
                List<Long> ownerIds = users.stream().map(User::getId).collect(java.util.stream.Collectors.toList());
                queryWrapper.in(Pet::getOwnerId, ownerIds);
            } else {
                return List.of(); // 搜不到主人，自然搜不到宠物
            }
        }

        // 2. 处理宠物名搜索
        if (petName != null && !petName.isEmpty()) {
            queryWrapper.like(Pet::getName, petName);
        }

        // 3. 权限逻辑隔离
        if (isAdmin) {
            return petService.list(queryWrapper);
        } else {
            String username = auth.getName();
            User currentUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (currentUser != null) {
                queryWrapper.eq(Pet::getOwnerId, currentUser.getId());
                return petService.list(queryWrapper);
            }
            return List.of();
        }
    }

    @GetMapping("/owner/{ownerId}")
    public List<Pet> getPetsByOwnerId(@PathVariable Long ownerId) {
        return petService.getPetsByOwnerId(ownerId);
    }

    @GetMapping("/{id}")
    public Pet getPetById(@PathVariable Long id) {
        return petService.getPetWithOwner(id);
    }

    @PutMapping("/{id}")
    public Pet updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        pet.setId(id);
        petService.updateById(pet);
        return pet;
    }

    @DeleteMapping("/{id}")
    public void deletePet(@PathVariable Long id) {
        petService.removeById(id);
    }
}