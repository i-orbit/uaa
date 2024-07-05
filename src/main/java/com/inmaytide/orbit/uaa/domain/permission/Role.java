package com.inmaytide.orbit.uaa.domain.permission;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
@Schema(title = "角色信息")
public class Role extends TombstoneEntity {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @Schema(title = "所属租户唯一标识", accessMode = Schema.AccessMode.READ_ONLY)
    private Long tenant;

    @Schema(title = "角色名称")
    private String name;

    @Schema(title = "角色编码", description = "租户唯一")
    private String code;

    @Schema(title = "角色说明", nullable = true)
    private String description;

    @Schema(title = "角色权重", description = "影响角色在列表中的排序")
    private BigDecimal weight;

    @TableField(exist = false)
    @Schema(title = "角色拥有的组织权限")
    private List<RoleAssociation> organizations;

    @TableField(exist = false)
    @Schema(title = "角色拥有的区域位置")
    private List<RoleAssociation> areas;

    @TableField(exist = false)
    @Schema(title = "角色拥有的操作权限")
    private List<RoleAssociation> authorities;

    @TableField(exist = false)
    @Schema(title = "角色拥有的WEB菜单权限")
    private List<RoleAssociation> webMenus;

    @TableField(exist = false)
    @Schema(title = "角色拥有的APP菜单权限")
    private List<RoleAssociation> appMenus;

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public List<RoleAssociation> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<RoleAssociation> organizations) {
        this.organizations = organizations;
    }

    public List<RoleAssociation> getAreas() {
        return areas;
    }

    public void setAreas(List<RoleAssociation> areas) {
        this.areas = areas;
    }

    public List<RoleAssociation> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<RoleAssociation> authorities) {
        this.authorities = authorities;
    }

    public List<RoleAssociation> getWebMenus() {
        return webMenus;
    }

    public void setWebMenus(List<RoleAssociation> webMenus) {
        this.webMenus = webMenus;
    }

    public List<RoleAssociation> getAppMenus() {
        return appMenus;
    }

    public void setAppMenus(List<RoleAssociation> appMenus) {
        this.appMenus = appMenus;
    }
}
