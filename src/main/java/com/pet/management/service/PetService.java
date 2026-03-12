package com.pet.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.management.entity.Pet;
import java.util.List;

public interface PetService extends IService<Pet> {
    List<Pet> getPetsByOwnerId(Long ownerId);
    Pet getPetWithOwner(Long petId);
}