package com.inmaytide.orbit.uaa.domain.permission;

import com.inmaytide.orbit.commons.constants.TenantState;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Schema(title = "租户信息")
public class Tenant extends TombstoneEntity {

    @Schema(title = "租户全称")
    private String name;

    @Schema(title = "租户别称/简称")
    private String alias;

    @Schema(title = "租户状态")
    private TenantState state;

    @Schema(title = "LOGO图片存储地址")
    private String logo;

    @Schema(title = "系统授权码")
    private String license;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public TenantState getState() {
        return state;
    }

    public void setState(TenantState state) {
        this.state = state;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
