package com.inmaytide.orbit.uaa.domain.permission;

import com.inmaytide.orbit.commons.domain.GeographicCoordinate;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Schema(title = "组织信息")
public class Organization extends TombstoneEntity {

    @Schema(title = "所属组织")
    private Long tenant;

    @Schema(title = "父级组织", description = "根目录值为 0")
    private Long parent;

    @Schema(title = "组织名称")
    private String name;

    @Schema(title = "组织别名/简称")
    private String alias;

    @Schema(title = "组织简介照片存储路径")
    private String cover;

    @Schema(title = "组织简介")
    private String description;

    @Schema(title = "组织地址")
    private String address;

    @Schema(title = "负责人")
    private Long principal;

    @Schema(title = "组织类型")
    private OrganizationType type;

    @Schema(title = "在同级别下的排序")
    private Integer sequence;

    @Schema(title = "组织所在树的ID路径", description = "自动根据数据结构生成, 其他方式修改无效", accessMode = Schema.AccessMode.READ_ONLY)
    private String path;

    @Schema(title = "地理位置")
    private GeographicCoordinate geolocation;

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPrincipal() {
        return principal;
    }

    public void setPrincipal(Long principal) {
        this.principal = principal;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public GeographicCoordinate getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeographicCoordinate geolocation) {
        this.geolocation = geolocation;
    }
}
