package com.inmaytide.orbit.uaa.domain.permission;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.uaa.consts.TenantPermissionCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import org.wildfly.common.annotation.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2024/7/22
 */
@Schema(title = "租户权限表")
public class TenantPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @NotNull
    @Schema(title = "租户唯一标识")
    private Long tenant;

    @NotNull
    @Schema(title = "租户权限类别")
    private TenantPermissionCategory category;

    @NotNull
    @Schema(title = "关联唯一标识")
    private String value;

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public TenantPermissionCategory getCategory() {
        return category;
    }

    public void setCategory(TenantPermissionCategory category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
