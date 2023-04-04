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
@ApiModel("用户与岗位关联管理表(用户岗位)")
public class AssociationUserAndPosition {

    @ApiModelProperty("用户唯一标识")
    private Long userId;

    @ApiModelProperty("用户姓名")
    @TableField(exist = false)
    private String username;

    @ApiModelProperty("岗位唯一标识")
    private Long positionId;

    @ApiModelProperty("岗位名称")
    @TableField(exist = false)
    private String positionName;

    @ApiModelProperty(value = "是否是该用户默认岗位", notes = "一个用户只能有一个默认岗位")
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

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Is getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Is isDefault) {
        this.isDefault = isDefault;
    }
}
