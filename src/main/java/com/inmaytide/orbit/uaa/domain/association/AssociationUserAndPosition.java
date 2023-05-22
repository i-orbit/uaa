package com.inmaytide.orbit.uaa.domain.association;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.consts.Is;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 用户与组织关联管理表(用户所属组织)
 *
 * @author inmaytide
 * @since 2023/4/4
 */
@Schema(title = "用户与岗位关联管理表(用户岗位)")
public class AssociationUserAndPosition implements Serializable {

    @Schema(title = "用户唯一标识")
    private Long userId;

    @Schema(title = "用户姓名")
    @TableField(exist = false)
    private String username;

    @Schema(title = "岗位唯一标识")
    private Long positionId;

    @Schema(title = "岗位名称")
    @TableField(exist = false)
    private String positionName;

    @Schema(title = "是否是该用户默认岗位", description = "一个用户只能有一个默认岗位")
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
