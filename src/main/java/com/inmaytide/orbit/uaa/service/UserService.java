package com.inmaytide.orbit.uaa.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.provider.UserDetailsProvider;
import com.inmaytide.orbit.uaa.domain.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
public interface UserService extends UserDetailsProvider, BasicService<User> {

    /**
     * 通过用户登录名查询指定用户信息
     * @param username 用户登录名(username)/邮箱地址(email)/手机号(telephoneNumber)/员工编号(employeeId)都可以作为用户登录名
     * @return 对应的用户信息
     */
    Optional<User> findUserByUsername(String username);


    Map<Long, String> findNamesByIds(List<Long> ids);

}
