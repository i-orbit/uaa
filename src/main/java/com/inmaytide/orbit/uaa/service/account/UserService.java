package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.business.SystemUserService;
import com.inmaytide.orbit.uaa.domain.account.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
public interface UserService extends BasicService<User> {

    Optional<User> findByLoginName(String loginName);

    Map<Long, String> findEmailsByIds(List<Long> ids);

    Map<Long, String> findTelephoneNumbersByIds(List<Long> ids);

    Map<Long, String> findNamesByIds(List<Long> ids);

}
