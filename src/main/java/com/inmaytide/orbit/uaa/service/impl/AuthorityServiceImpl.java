package com.inmaytide.orbit.uaa.service.impl;

import com.inmaytide.orbit.uaa.domain.User;
import com.inmaytide.orbit.uaa.service.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {
    @Override
    public List<String> findAuthoritiesByUser(User user) {
        return Collections.emptyList();
    }
}
