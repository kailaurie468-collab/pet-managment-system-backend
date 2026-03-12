package com.pet.management;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        String adminPassword = passwordEncoder.encode("admin123");
        String ownerPassword = passwordEncoder.encode("owner123");
        
        System.out.println("Admin password: " + adminPassword);
        System.out.println("Owner password: " + ownerPassword);
    }
}