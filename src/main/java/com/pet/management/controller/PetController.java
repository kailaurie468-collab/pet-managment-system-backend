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
    public List<Pet> getAllPets() {
        // 管理员可查看所有宠物
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return petService.list();
        }
        // 普通用户只能看到自己的宠物
        String username = auth.getName();
        User currentUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (currentUser != null) {
            return petService.getPetsByOwnerId(currentUser.getId());
        }
        return List.of();
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