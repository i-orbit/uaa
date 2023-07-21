package com.inmaytide.orbit.uaa.domain.role;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Schema(title = "角色信息")
public class Role extends TombstoneEntity {

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "所属租户", accessMode = Schema.AccessMode.READ_ONLY)
    private Long tenant;

    @NotBlank
    @Schema(title = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 64)
    private String name;

    @NotBlank
    @Schema(title = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 64)
    private String code;

    @Length(max = 512)
    @Schema(title = "角色描述", maxLength = 512)
    private String description;

    @Min(0)
    @Max(100)
    @Schema(title = "角色权重", description = "影响角色列表排序", defaultValue = "100", maximum = "100", minimum = "0")
    private Integer weights;

    @TableField(exist = false)
    @Schema(title = "角色拥有的组织权限")
    public List<Long> organizations;

    @TableField(exist = false)
    @Schema(title = "角色的Web菜单权限")
    private List<Long> webMenus;

    @TableField(exist = false)
    @Schema(title = "角色的App菜单权限")
    private List<Long> appMenus;

    @TableField(exist = false)
    @Schema(title = "角色的功能权限")
    private List<Long> authorities;

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

    public Integer getWeights() {
        return weights;
    }

    public void setWeights(Integer weights) {
        this.weights = weights;
    }

    public List<Long> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Long> organizations) {
        this.organizations = organizations;
    }

    public List<Long> getWebMenus() {
        return webMenus;
    }

    public void setWebMenus(List<Long> webMenus) {
        this.webMenus = webMenus;
    }

    public List<Long> getAppMenus() {
        return appMenus;
    }

    public void setAppMenus(List<Long> appMenus) {
        this.appMenus = appMenus;
    }

    public List<Long> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Long> authorities) {
        this.authorities = authorities;
    }
}
