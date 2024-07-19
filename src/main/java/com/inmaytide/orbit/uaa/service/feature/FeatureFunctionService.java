package com.inmaytide.orbit.uaa.service.feature;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.feature.FeatureFunction;
import com.inmaytide.orbit.uaa.domain.feature.FeatureMenu;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
public interface FeatureFunctionService extends BasicService<FeatureFunction> {

    void persist(FeatureMenu menu);

}
