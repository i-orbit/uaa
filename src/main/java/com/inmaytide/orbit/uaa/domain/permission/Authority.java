package com.inmaytide.orbit.uaa.domain.permission;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;

/**
 * @author inmaytide
 * @since 2024/5/31
 */
@Schema(title = "功能权限管理")
public class Authority extends TombstoneEntity {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @Schema(title = "功能权限编码", description = "全局唯一")
    private String code;

    @Schema(title = "功能权限名称")
    private String name;

    @Schema(title = "WEB菜单唯一标识")
    private Long webMenuId;

    @Schema(title = "APP菜单唯一标识")
    private Long appMenuId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWebMenuId() {
        return webMenuId;
    }

    public void setWebMenuId(Long webMenuId) {
        this.webMenuId = webMenuId;
    }

    public Long getAppMenuId() {
        return appMenuId;
    }

    public void setAppMenuId(Long appMenuId) {
        this.appMenuId = appMenuId;
    }
}
