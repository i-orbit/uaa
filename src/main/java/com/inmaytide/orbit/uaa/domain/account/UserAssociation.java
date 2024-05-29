package com.inmaytide.orbit.uaa.domain.account;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import org.wildfly.common.annotation.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

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

    public static UserAssociationBuilder builder(UserAssociationCategory category) {
        UserAssociationBuilder builder = new UserAssociationBuilder();
        return builder.category(category);
    }

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

    public static class UserAssociationBuilder {

        private User user;

        private UserAssociationCategory category;

        private Entity associated;

        private boolean defaulted = false;

        public UserAssociationBuilder category(UserAssociationCategory category) {
            this.category = category;
            return this;
        }

        public UserAssociationBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserAssociationBuilder associated(Entity associated) {
            this.associated = associated;
            return this;
        }

        public UserAssociationBuilder defaulted(boolean defaulted) {
            this.defaulted = defaulted;
            return this;
        }

        public UserAssociation build() {
            UserAssociation association = new UserAssociation();
            association.setCategory(Objects.requireNonNull(category, "category is required"));
            association.setUser(Objects.requireNonNull(user, "user is required").getId());
            association.setAssociated(Objects.requireNonNull(associated, "associated is required").getId());
            association.setDefaulted(Bool.withValue(defaulted));
            return association;
        }

    }


}
