package com.pet.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("service_record")
public class ServiceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long petId;
    private Long serviceId;
    private Long doctorId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    private Date serviceDate;
    private Double amount;
    private String notes;
}