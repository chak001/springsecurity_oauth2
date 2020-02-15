package com.itheima.mapper;

import com.itheima.domain.SysUser;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: Eric
 * @Date: 2020/2/5 15:15
 */
@Repository
public interface UserMapper extends Mapper<SysUser> {


    @Select("select * from sys_user where username=#{username}")
   @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "roles", column = "id", javaType = List.class,
                    many = @Many(select = "com.itheima.mapper.RoleMapper.findByUid"))
    })
    public SysUser findByUsername(String username);
}
