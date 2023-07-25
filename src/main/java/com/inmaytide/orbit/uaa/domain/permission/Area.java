package com.inmaytide.orbit.uaa.domain.permission;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.domain.GeographicCoordinate;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Schema(title = "区域位置")
public class Area extends TombstoneEntity {

    @Schema(title = "父级区域唯一标识", description = "根目录为值为 0")
    private Long parent;

    @Schema(title = "所属租户")
    private Long tenant;

    @NotBlank
    @Schema(title = "区域位置名称")
    private String name;

    @Schema(title = "区域位置别名或简称")
    private String alias;

    @Schema(title = "区域位置简介照片存储路径")
    private String cover;

    @Schema(title = "区域位置简介说明")
    private String description;

    @Schema(title = "地址")
    private String address;

    @NotNull
    @Schema(title = "区域位置类型")
    private AreaType type = AreaType.PROCESS;

    @NotBlank
    @Schema(title = "区域位置类别", description = "取类别[code=AREA_CATEGORY]的数据字典信息")
    private String category;

    @Schema(title = "区域位置类别描述")
    @TableField(exist = false)
    private String categoryName;

    @Schema(title = "在同级别下的排序")
    private Integer sequence;

    @Schema(title = "区域位置所在树的ID路径", description = "自动根据数据结构生成, 其他方式修改无效", accessMode = Schema.AccessMode.READ_ONLY)
    private String path;

    @TableField(exist = false)
    @Schema(title = "区域位置所在地理位置", description = "一个(位置)或多个(区域)坐标点")
    private List<GeographicCoordinate> geolocation;

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
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

    public AreaType getType() {
        return type;
    }

    public void setType(AreaType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public List<GeographicCoordinate> getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(List<GeographicCoordinate> geolocation) {
        this.geolocation = geolocation;
    }
}
