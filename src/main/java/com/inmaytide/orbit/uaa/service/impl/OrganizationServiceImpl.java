package com.inmaytide.orbit.uaa.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.Organization;
import com.inmaytide.orbit.uaa.service.OrganizationService;
import org.springframework.stereotype.Service;

/**
 * @author inmaytide
 * @since 2023/6/27
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {
    @Override
    public BaseMapper<Organization> getMapper() {
        return null;
    }

    @Override
    public Class<Organization> getEntityClass() {
        return null;
    }
}
