package com.inmaytide.orbit.uaa.service.permission.dto;

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

    private String statuses;

    @Override
    public LambdaQueryWrapper<Tenant> toWrapper() {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(getQueryName())) {
            wrapper.and(w -> w.like(Tenant::getName, getQueryName()).or().like(Tenant::getAlias, getQueryName()));
        }
        wrapper.in(StringUtils.isNotBlank(getStatuses()), Tenant::getStatus, CommonUtils.splitByCommas(getStatuses()));
        wrapper.orderByDesc(Tenant::getCreatedAt);
        return null;
    }

    public String getStatuses() {
        return statuses;
    }

    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }
}
