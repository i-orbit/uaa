package com.inmaytide.orbit.uaa.domain.account;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import org.wildfly.common.annotation.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
@Schema(title = "用户附属信息关联表")
public class UserAssociation implements Serializable {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @NotNull
    @Schema(title = "用户唯一标识")
    private Long user;

    @NotNull
    @Schema(title = "关联唯一标识")
    private Long associated;

    @NotNull
    @Schema(title = "关联信息类别")
    private UserAssociationCategory category;

    @Schema(title = "是否用户默认", description = "例如标识用户默认组织等")
    private Bool defaulted;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getAssociated() {
        return associated;
    }

    public void setAssociated(Long associated) {
        this.associated = associated;
    }

    public UserAssociationCategory getCategory() {
        return category;
    }

    public void setCategory(UserAssociationCategory category) {
        this.category = category;
    }

    public Bool getDefaulted() {
        return defaulted;
    }

    public void setDefaulted(Bool defaulted) {
        this.defaulted = defaulted;
    }
}
