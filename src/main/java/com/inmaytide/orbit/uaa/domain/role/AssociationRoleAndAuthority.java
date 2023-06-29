package com.inmaytide.orbit.uaa.domain.role;

import com.inmaytide.orbit.uaa.domain.consts.AuthorityCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Schema(title = "角色关联的权限信息(包括功能权限和数据权限)")
public class AssociationRoleAndAuthority {

    @Schema(title = "角色唯一标识")
    private Long roleId;

    @Schema(title = "权限分类")
    private AuthorityCategory category;

    @Schema(title = "关联权限实例标识", description = "组织唯一标识/菜单编码/功能权限编码等")
    private Serializable association;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public AuthorityCategory getCategory() {
        return category;
    }

    public void setCategory(AuthorityCategory category) {
        this.category = category;
    }

    public Serializable getAssociation() {
        return association;
    }

    public void setAssociation(Serializable association) {
        this.association = association;
    }
}
