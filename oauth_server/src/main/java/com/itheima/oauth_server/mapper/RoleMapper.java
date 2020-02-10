package com.itheima.oauth_server.mapper;

import com.itheima.oauth_server.domain.SysRole;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: Eric
 * @Date: 2020/2/5 17:55
 */
public interface RoleMapper extends Mapper<SysRole> {

    @Select("select r.id,r.role_name roleName ,r.role_desc roleDesc " +
            "FROM sys_role r,sys_user_role ur " +
            "WHERE r.id=ur.rid AND ur.uid=#{uid}")
    public List<SysRole> findByUid(Integer uid);
}
