package com.inmaytide.orbit.uaa.service.permission.impl;

import com.inmaytide.orbit.uaa.service.permission.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Override
    public List<String> findCodesByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    @Override
    public List<String> findCodesByRoleCodes(List<String> roleCodes) {
        return new ArrayList<>();
    }

}
