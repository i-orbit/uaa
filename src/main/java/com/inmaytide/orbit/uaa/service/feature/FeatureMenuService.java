package com.inmaytide.orbit.uaa.service.feature;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.feature.Feature;
import com.inmaytide.orbit.uaa.domain.feature.FeatureMenu;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
public interface FeatureMenuService extends BasicService<FeatureMenu> {

    void persist(Feature entity);

}
