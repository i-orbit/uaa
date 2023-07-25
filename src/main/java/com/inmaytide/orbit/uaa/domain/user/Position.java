package com.inmaytide.orbit.uaa.domain.user;

import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Schema(title = "岗位信息")
public class Position extends TombstoneEntity {

    @Schema(title = "所属租户")
    private Long tenant;

    @NotBlank
    @Schema(title = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 64)
    private String name;

    @NotBlank
    @Schema(title = "岗位编码", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 64)
    private String code;

    @Schema(title = "岗位描述", maxLength = 512)
    private String description;

    @Max(100)
    @Min(0)
    @Schema(title = "岗位权重", description = "影响岗位列表排序", defaultValue = "100", maximum = "100", minimum = "0")
    private Integer weights;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWeights() {
        return weights;
    }

    public void setWeights(Integer weights) {
        this.weights = weights;
    }
}
