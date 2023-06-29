package com.inmaytide.orbit.uaa.domain.organization;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Schema(title = "组织与区域位置关联关系")
public class AssociationOrganizationAndArea implements Serializable {

    @Schema(title = "组织唯一标识")
    private Long organizationId;

    @Schema(title = "区域唯一标识")
    private Long areaId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }
}
