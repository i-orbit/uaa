package com.inmaytide.orbit.uaa.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.security.UserDetailsService;
import com.inmaytide.orbit.uaa.domain.User;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
public interface UserService extends UserDetailsService, BasicService<User> {



}
