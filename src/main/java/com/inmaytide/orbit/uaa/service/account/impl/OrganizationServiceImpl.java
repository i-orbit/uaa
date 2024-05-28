package com.inmaytide.orbit.uaa.service.account.impl;

import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.uaa.domain.account.Organization;
import com.inmaytide.orbit.uaa.mapper.account.OrganizationMapper;
import com.inmaytide.orbit.uaa.service.account.OrganizationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
@Service
public class OrganizationServiceImpl extends BasicServiceImpl<OrganizationMapper, Organization> implements OrganizationService {
    
    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        return findFieldValueByIds(ids, Organization::getName);
    }

}
