package com.inmaytide.orbit.uaa.domain.feature;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
@Schema(title = "系统功能模块")
public class Feature extends TombstoneEntity {

    @Schema(title = "功能模块名称")
    private String name;

    @Schema(title = "功能模块编码")
    private String code;

    @Schema(title = "父功能模块唯一标识, 无上级默认为 ROOT")
    private String parent;

    @Schema(title = "功能介绍")
    private String description;

    @Schema(title = "是否必须功能", description = "默认为否, 如果为是时-给租户分配功能权限时默认选中且不可取消")
    private Bool necessary;

    @Schema(title = "排序字段", description = "同级别唯一")
    private Integer sequence;

    @TableField(exist = false)
    @Schema(title = "模块包含的功能菜单列表")
    private List<FeatureMenu> menus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bool getNecessary() {
        return necessary;
    }

    public void setNecessary(Bool necessary) {
        this.necessary = necessary;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<FeatureMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<FeatureMenu> menus) {
        this.menus = menus;
    }
}
