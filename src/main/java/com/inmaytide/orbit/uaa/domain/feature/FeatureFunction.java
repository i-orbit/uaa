package com.inmaytide.orbit.uaa.domain.feature;

import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
@Schema(title = "系统功能模块-菜单功能点")
public class FeatureFunction extends TombstoneEntity {

    @Schema(title = "功能点编码", description = "全局唯一")
    private String code;

    @Schema(title = "功能点名称")
    private String name;

    @Schema(title = "排序字段")
    private Integer sequence;

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

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
