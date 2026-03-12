package com.pet.management.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 生成 BCrypt 加密密码
        String adminPassword = passwordEncoder.encode("admin123");
        String ownerPassword = passwordEncoder.encode("owner123");
        
        System.out.println("BCrypt 加密后的 admin123: " + adminPassword);
        System.out.println("BCrypt 加密后的 owner123: " + ownerPassword);
        
        // 验证密码
        System.out.println("\n验证密码:");
        System.out.println("admin123 匹配：" + passwordEncoder.matches("admin123", adminPassword));
        System.out.println("owner123 匹配：" + passwordEncoder.matches("owner123", ownerPassword));
    }
}
