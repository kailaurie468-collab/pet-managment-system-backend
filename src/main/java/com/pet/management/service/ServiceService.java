package com.pet.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.management.entity.Service;
import java.util.List;

public interface ServiceService extends IService<Service> {
    List<Service> getServicesByCategory(String category);
}