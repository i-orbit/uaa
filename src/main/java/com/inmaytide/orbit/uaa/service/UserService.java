package com.inmaytide.orbit.uaa.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.security.UserDetailsService;
import com.inmaytide.orbit.uaa.domain.User;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
public interface UserService extends UserDetailsService, BasicService<User> {

    User findUserByUsername(String username);

    Map<Long, String> findNamesByIds(List<Long> ids);

}
