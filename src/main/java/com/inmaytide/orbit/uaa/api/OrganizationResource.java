package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.organization.Organization;
import com.inmaytide.orbit.uaa.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author inmaytide
 * @since 2023/6/26
 */
@RestController
@Tag(name = "组织信息")
@RequestMapping("/api/organizations")
public class OrganizationResource {

    private final OrganizationService service;

    public OrganizationResource(OrganizationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "新增组织信息")
    public Organization create(@RequestBody @Validated Organization entity) {
        return service.create(entity);
    }

    @PutMapping
    @Operation(summary = "修改组织信息")
    public Organization update(@RequestBody @Validated Organization entity) {
        return service.update(entity);
    }

    @DeleteMapping
    @Operation(summary = "删除指定组织信息置")
    public AffectedResult deleteByIds(@Parameter(description = "指定组织的唯一标识, 多个英文逗号隔开", required = true) @RequestParam String ids) {
        return service.deleteByIds(CommonUtils.splitToLongByCommas(ids));
    }

    @GetMapping
    @Operation(summary = "查询组织信息树")
    public List<TreeNode<Organization>> getTreeOfOrganizations() {
        return service.getTreeOfOrganizations();
    }

}
