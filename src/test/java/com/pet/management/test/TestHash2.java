package com.pet.management.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestHash2 {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 打印一个新的 admin123 密文
        String newHash = encoder.encode("admin123");
        System.out.println("New Hash for 'admin123': " + newHash);

        // 测试旧密文和 admin123
        boolean matches = encoder.matches("admin123", "$2a$10$N9qo8uLOickgx2ZMQZpT5eTKb9vXJxH4jLcJz7VVlJKrPQwqLJh6K");
        System.out.println("Old Hash Matches: " + matches);
    }
}
