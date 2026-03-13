package com.pet.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.management.entity.User;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);
}