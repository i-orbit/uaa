package com.inmaytide.orbit.uaa.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.uaa.domain.organization.Organization;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/6/27
 */
public interface OrganizationService extends BasicService<Organization> {

    String CACHE_NAME_ALL_ORGANIZATIONS = "ALL_ORGANIZATIONS";

    String TREE_NODE_SYMBOL = "organization";

    List<Organization> all(Long tenant);

    List<TreeNode<Organization>> getTreeOfOrganizations();

    Map<Long, String> findNamesByIds(List<Long> ids);
}
