package com.inmaytide.orbit.uaa.domain.organization;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.uaa.domain.consts.OrganizationAssociationCategory;
import com.inmaytide.orbit.uaa.domain.consts.UserAssociationCategory;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Schema(title = "组织关联其他数据实例关联关系表")
public class OrganizationAssociation {

    @Schema(title = "组织唯一标识")
    private Long organization;

    @Schema(title = "组织名称")
    @TableField(exist = false)
    private String organizationName;

    @Schema(title = "组织关联数据类型")
    private OrganizationAssociationCategory category;

    @Schema(title = "关联数据实例唯一标识")
    private Long associated;

    @Schema(title = "关联数据实例展示名称")
    private String associatedName;

    public Long getOrganization() {
        return organization;
    }

    public void setOrganization(Long organization) {
        this.organization = organization;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public OrganizationAssociationCategory getCategory() {
        return category;
    }

    public void setCategory(OrganizationAssociationCategory category) {
        this.category = category;
    }

    public Long getAssociated() {
        return associated;
    }

    public void setAssociated(Long associated) {
        this.associated = associated;
    }

    public String getAssociatedName() {
        return associatedName;
    }

    public void setAssociatedName(String associatedName) {
        this.associatedName = associatedName;
    }
}
