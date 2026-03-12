package com.pet.management.controller;

import com.pet.management.entity.Service;
import com.pet.management.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    @PostMapping
    public Service createService(@RequestBody Service service) {
        serviceService.save(service);
        return service;
    }

    @GetMapping
    public List<Service> getAllServices() {
        return serviceService.list();
    }

    @GetMapping("/category/{category}")
    public List<Service> getServicesByCategory(@PathVariable String category) {
        return serviceService.getServicesByCategory(category);
    }

    @GetMapping("/{id}")
    public Service getServiceById(@PathVariable Long id) {
        return serviceService.getById(id);
    }

    @PutMapping("/{id}")
    public Service updateService(@PathVariable Long id, @RequestBody Service service) {
        service.setId(id);
        serviceService.updateById(service);
        return service;
    }

    @DeleteMapping("/{id}")
    public void deleteService(@PathVariable Long id) {
        serviceService.removeById(id);
    }
}