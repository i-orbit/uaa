package com.inmaytide.orbit.uaa.domain.permission;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.constants.RoleAssociationCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import org.wildfly.common.annotation.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2024/5/31
 */
@Schema(title = "角色关联权限配置表")
public class RoleAssociation implements Serializable {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @NotNull
    @Schema(title = "角色唯一标识")
    private String role;

    @NotNull
    @Schema(title = "关联唯一标识")
    private String associated;

    @NotNull
    @Schema(title = "关联信息类别")
    private RoleAssociationCategory category;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAssociated() {
        return associated;
    }

    public void setAssociated(String associated) {
        this.associated = associated;
    }

    public RoleAssociationCategory getCategory() {
        return category;
    }

    public void setCategory(RoleAssociationCategory category) {
        this.category = category;
    }
}
