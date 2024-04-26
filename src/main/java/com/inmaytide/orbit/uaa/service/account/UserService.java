package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.business.SystemUserService;
import com.inmaytide.orbit.uaa.domain.account.User;

import java.util.Optional;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
public interface UserService extends BasicService<User>, SystemUserService {

    Optional<User> findByLoginName(String loginName);

}
