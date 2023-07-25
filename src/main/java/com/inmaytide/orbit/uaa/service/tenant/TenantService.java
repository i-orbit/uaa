package com.inmaytide.orbit.uaa.service.tenant;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.tenant.Tenant;

import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
public interface TenantService extends BasicService<Tenant> {

    Map<Long, String> getNamesByIds(String ids);

}
