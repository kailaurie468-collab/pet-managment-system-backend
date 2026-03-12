package com.pet.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.management.entity.User;
import com.pet.management.mapper.UserMapper;
import com.pet.management.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User findByUsername(String username) {
        return baseMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<User>()
                .eq(User::getRole, role)
        );
    }
}