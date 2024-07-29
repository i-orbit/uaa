package com.inmaytide.orbit.uaa.domain.feature;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.constants.Platforms;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
@Schema(title = "功能菜单")
public class FeatureMenu extends TombstoneEntity {

    @Schema(title = "所属功能模块编码")
    private String featureCode;

    @Schema(title = "菜单名称")
    private String name;

    @Schema(title = "菜单编码", description = "系统全局唯一")
    private String code;

    @Schema(title = "菜单归属平台(WEB/APP等)")
    private Platforms platform;

    @Schema(title = "排序字段")
    private Integer sequence;

    @Schema(title = "父级菜单, 无上级菜单为 ROOT")
    private String parent;

    @TableField("`URL`")
    @Schema(title = "菜单URL")
    private String URL;

    @Schema(title = "打开方式", description = "如WEB端_blank和_self等")
    private String target;

    @Schema(title = "菜单图标")
    private String icon;

    @TableField(exist = false)
    @Schema(title = "菜单功能点")
    private List<FeatureFunction> functions;

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

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

    public Platforms getPlatform() {
        return platform;
    }

    public void setPlatform(Platforms platform) {
        this.platform = platform;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<FeatureFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FeatureFunction> functions) {
        this.functions = functions;
    }
}
