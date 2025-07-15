package com.inmaytide.orbit.uaa.domain.permission;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.domain.GeographicCoordinate;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import com.inmaytide.orbit.uaa.consts.OrganizationCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author inmaytide
 * @since 2024/5/27
 */
@Schema(title = "组织信息")
public class Organization extends TombstoneEntity {

    @Schema(title = "所属租户唯一标识", accessMode = Schema.AccessMode.READ_ONLY)
    private String tenant;

    @NotBlank
    @Schema(title = "组织名称")
    private String name;

    @NotBlank
    @Schema(title = "组织编码", description = "租户唯一")
    private String code;

    @Schema(title = "上级组织")
    private String parent;

    @NotNull
    @Schema(title = "组织分类")
    private OrganizationCategory category;

    @Schema(title = "简介")
    private String introduction;

    @Schema(title = "鸟瞰/封面图片地址")
    private String cover;

    @Schema(title = "负责人")
    private String principal;

    @TableField(exist = false)
    @Schema(title = "负责人姓名")
    private String principalName;

    @Schema(title = "排序字段")
    private Integer sequence;

    @Valid
    @Schema(title = "地理位置")
    private GeographicCoordinate location;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public OrganizationCategory getCategory() {
        return category;
    }

    public void setCategory(OrganizationCategory category) {
        this.category = category;
    }

    public String getCategoryName() {
        return category == null ? null : category.getName();
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public GeographicCoordinate getLocation() {
        return location;
    }

    public void setLocation(GeographicCoordinate location) {
        this.location = location;
    }
}
