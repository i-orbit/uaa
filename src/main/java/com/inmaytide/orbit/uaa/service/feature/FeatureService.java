package com.inmaytide.orbit.uaa.service.feature;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.uaa.domain.feature.Feature;

import java.util.TreeSet;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
public interface FeatureService extends BasicService<Feature> {

    TreeSet<TreeNode<Object>> treeOfFeatures(Bool includeMenus);

}
