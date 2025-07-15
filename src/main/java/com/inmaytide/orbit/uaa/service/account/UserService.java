package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.account.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
public interface UserService extends BasicService<User> {

    String BUSINESS_KEY = "SYS_USER";

    Optional<User> findByLoginName(String loginName);

    Map<String, String> findEmailsByIds(List<String> ids);

    Map<String, String> findTelephoneNumbersByIds(List<String> ids);

    Map<String, String> findNamesByIds(List<String> ids);

}
