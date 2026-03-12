package com.pet.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.management.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
}
