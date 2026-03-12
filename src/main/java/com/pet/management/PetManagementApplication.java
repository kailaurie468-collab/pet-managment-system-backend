package com.pet.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.pet.management.mapper")
public class PetManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetManagementApplication.class, args);
    }
}