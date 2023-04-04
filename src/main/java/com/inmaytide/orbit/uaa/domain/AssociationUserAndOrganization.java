package com.inmaytide.orbit.uaa.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.consts.Is;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户与组织关联管理表(用户所属组织)
 *
 * @author inmaytide
 * @since 2023/4/4
 */
@ApiModel("用户与组织关联管理表(用户所属组织)")
public class AssociationUserAndOrganization {

    @ApiModelProperty("用户唯一标识")
    private Long userId;

    @ApiModelProperty("用户姓名")
    @TableField(exist = false)
    private String username;

    @ApiModelProperty("组织唯一标识")
    private Long organizationId;

    @ApiModelProperty("组织名称")
    @TableField(exist = false)
    private String organizationName;

    @ApiModelProperty(value = "是否是该用户默认组织", notes = "一个用户只能有一个默认组织")
    private Is isDefault;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Is getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Is isDefault) {
        this.isDefault = isDefault;
    }
}
