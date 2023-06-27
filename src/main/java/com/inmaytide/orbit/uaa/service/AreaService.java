package com.inmaytide.orbit.uaa.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.uaa.domain.Area;

import java.util.List;

/**
 * @author inmaytide
 * @since 2023/6/26
 */
public interface AreaService extends BasicService<Area> {

    String CACHE_NAME_ALL_AREAS = "ALL_AREAS";

    List<Area> all(Long tenant);

    List<TreeNode<Area>> getTreeOfAreas();
}
