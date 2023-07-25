package com.inmaytide.orbit.uaa.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.consts.Is;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Schema(title = "用户关联其他数据实例关联关系表")
public class UserAssociation {

    @Schema(title = "用户唯一标识")
    private Long user;

    @Schema(title = "用户姓名")
    @TableField(exist = false)
    private String username;

    @Schema(title = "用户关联数据类型")
    private UserAssociationCategory category;

    @Schema(title = "关联数据实例唯一标识")
    private Long associated;

    @Schema(title = "关联数据实例展示名称")
    private String associatedName;

    @Schema(title = "是否默认", description = "如默认组织, 默认岗位等")
    private Is defaulted;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserAssociationCategory getCategory() {
        return category;
    }

    public void setCategory(UserAssociationCategory category) {
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

    public Is getDefaulted() {
        return defaulted;
    }

    public void setDefaulted(Is defaulted) {
        this.defaulted = defaulted;
    }
}
