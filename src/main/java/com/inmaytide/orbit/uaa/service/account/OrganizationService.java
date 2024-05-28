package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.account.Organization;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
public interface OrganizationService extends BasicService<Organization> {

    Map<Long, String> findNamesByIds(List<Long> ids);

}
