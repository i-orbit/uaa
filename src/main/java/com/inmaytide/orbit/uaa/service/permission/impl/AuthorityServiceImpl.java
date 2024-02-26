package com.inmaytide.orbit.uaa.service.permission.impl;

import com.inmaytide.orbit.uaa.service.permission.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Override
    public List<String> findCodesByRoleCodes(List<String> roleCodes) {
        return Collections.emptyList();
    }

}
