package com.inmaytide.orbit.uaa.service.permission.dto.TenantQuery;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.params.Pageable;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.permission.Tenant;
import org.apache.commons.lang3.StringUtils;

/**
 * @author inmaytide
 * @since 2024/7/12
 */
public class TenantQuery extends Pageable<Tenant> {

    private String states;

    @Override
    public LambdaQueryWrapper<Tenant> toWrapper() {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(getQueryName())) {
            wrapper.and(w -> w.like(Tenant::getName, getQueryName()).or().like(Tenant::getAlias, getQueryName()));
        }
        wrapper.in(StringUtils.isNotBlank(getStates()), Tenant::getState, CommonUtils.splitByCommas(getStates()));
        wrapper.orderByDesc(Tenant::getCreatedTime);
        return null;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }
}
