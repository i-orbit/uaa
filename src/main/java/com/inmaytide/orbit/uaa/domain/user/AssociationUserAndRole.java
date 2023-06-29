package com.inmaytide.orbit.uaa.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Schema(title = "用户与角色关联管理表(用户角色)")
public class AssociationUserAndRole implements Serializable {

    @Schema(title = "用户唯一标识")
    private Long userId;

    @Schema(title = "用户姓名")
    @TableField(exist = false)
    private String username;

    @Schema(title = "角色唯一标识")
    private Long roleId;

    @Schema(title = "角色名称")
    @TableField(exist = false)
    private String roleName;

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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
