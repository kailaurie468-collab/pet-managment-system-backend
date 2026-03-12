package com.pet.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.management.entity.Service;
import com.pet.management.mapper.ServiceMapper;
import com.pet.management.service.ServiceService;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, Service> implements ServiceService {
    @Override
    public List<Service> getServicesByCategory(String category) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<Service>()
                .eq(Service::getCategory, category)
        );
    }
}