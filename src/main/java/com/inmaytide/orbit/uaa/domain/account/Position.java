package com.inmaytide.orbit.uaa.domain.account;

import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.wildfly.common.annotation.NotNull;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
@Schema(title = "岗位信息")
public class Position extends TombstoneEntity {

    @Schema(title = "所属租户唯一标识", accessMode = Schema.AccessMode.READ_ONLY)
    private Long tenant;

    @NotBlank
    @Schema(title = "岗位编码", description = "租户唯一")
    private String code;

    @NotBlank
    @Schema(title = "岗位名称")
    private String name;

    @NotNull
    @Schema(title = "所属组织")
    private Long organization;

    @Schema(title = "上级岗位")
    private Long parent;

    @Schema(title = "岗位职责")
    private String responsibilities;

    @Schema(title = "排序字段")
    private Integer sequence;

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

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

    public Long getOrganization() {
        return organization;
    }

    public void setOrganization(Long organization) {
        this.organization = organization;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
