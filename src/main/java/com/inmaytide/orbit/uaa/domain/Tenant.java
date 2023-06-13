package com.inmaytide.orbit.uaa.domain;

import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.TenantState;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Schema(title = "租户信息")
public class Tenant extends TombstoneEntity {

    @NotBlank
    @Schema(title = "租户名称")
    private String name;

    @Schema(title = "租户别名(简称)", description = "不填默认等于租户名称")
    private String alias;

    @Schema(title = "租户状态")
    private TenantState state;

    @Schema(title = "LOGO图片存储地址")
    private String logo;

    @Schema(title = "定制化菜单是否已同步生成", accessMode = Schema.AccessMode.READ_ONLY)
    private Is menuSynced;

    @Schema(title = "license文件地址或license内容")
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

    public Is getMenuSynced() {
        return menuSynced;
    }

    public void setMenuSynced(Is menuSynced) {
        this.menuSynced = menuSynced;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
