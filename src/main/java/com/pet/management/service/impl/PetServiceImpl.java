package com.pet.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.management.entity.Pet;
import com.pet.management.mapper.PetMapper;
import com.pet.management.service.PetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {
    @Override
    public List<Pet> getPetsByOwnerId(Long ownerId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<Pet>()
                .eq(Pet::getOwnerId, ownerId)
        );
    }

    @Override
    public Pet getPetWithOwner(Long petId) {
        return baseMapper.selectById(petId);
    }
}