package com.pet.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.management.entity.User;
import java.util.List;

public interface UserService extends IService<User> {
    User findByUsername(String username);
    List<User> getUsersByRole(String role);
}