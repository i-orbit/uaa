package com.inmaytide.orbit.uaa.service.permission;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.uaa.domain.permission.Organization;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
public interface OrganizationService extends BasicService<Organization> {

    String SYMBOL = "ORGANIZATION";

    Map<Long, String> findNamesByIds(List<Long> ids);

    TreeSet<TreeNode<Organization>> treeOfOrganizations();

    List<Long> findAuthorizedIds(SystemUser user);
}
