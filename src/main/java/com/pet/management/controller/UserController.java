package com.pet.management.controller;

import com.pet.management.entity.User;
import com.pet.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public User createUser(@RequestBody User user) {
        // 校验用户名是否已存在
        if (userService.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名 '" + user.getUsername() + "' 已存在，请更换一个哦 🐾");
        }
        // 对密码进行 BCrypt 加密后再存库
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String identCard) {
        if ((name != null && !name.isEmpty()) || 
            (address != null && !address.isEmpty()) || 
            (identCard != null && !identCard.isEmpty())) {
            return userService.lambdaQuery()
                    .like(name != null && !name.isEmpty(), User::getName, name)
                    .like(address != null && !address.isEmpty(), User::getAddress, address)
                    .like(identCard != null && !identCard.isEmpty(), User::getIdentCard, identCard)
                    .list();
        }
        return userService.list();
    }

    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(@PathVariable String role) {
        return userService.getUsersByRole(role);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        // 如果提交了新密码则加密，否则不更新密码字段
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.removeById(id);
    }
}