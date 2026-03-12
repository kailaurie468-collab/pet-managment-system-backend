package com.pet.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("service")
public class Service {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
}