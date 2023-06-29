package com.inmaytide.orbit.uaa.service;

import com.inmaytide.orbit.uaa.domain.user.User;

import java.util.List;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
public interface AuthorityService {

    List<String> findAuthoritiesByUser(User user);

}
