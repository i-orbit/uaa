package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.domain.validation.groups.Add;
import com.inmaytide.orbit.commons.domain.validation.groups.Update;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.permission.Organization;
import com.inmaytide.orbit.uaa.service.permission.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.TreeSet;

/**
 * @author inmaytide
 * @since 2024/5/31
 */
@Tag(name = "组织架构")
@RestController
@RequestMapping("/api/organizations")
public class OrganizationResource {

    private final OrganizationService organizationService;

    public OrganizationResource(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    @Operation(summary = "新增组织信息")
    public Organization create(@Validated(Add.class) @RequestBody Organization organization) {
        return organizationService.create(organization);
    }

    @PutMapping
    @Operation(summary = "修改组织信息")
    public Organization update(@Validated(Update.class) @RequestBody Organization organization) {
        return organizationService.update(organization);
    }

    @DeleteMapping
    @Operation(summary = "删除指定组织信息")
    public AffectedResult deleteByIds(@RequestParam String ids) {
        return organizationService.deleteByIds(CommonUtils.splitByCommas(ids));
    }

    @GetMapping("tree-of-organizations")
    @Operation(summary = "查询当前用户有权限的组织架构树", description = "包含部分组成树结构时必要的无权限组织")
    public TreeSet<TreeNode<Organization>> treeOfOrganizations() {
        return organizationService.treeOfOrganizations();
    }

    @GetMapping("names")
    @Operation(summary = "查询指定组织名称")
    public Map<String, String> findNamesByIds(@RequestParam String ids) {
        return organizationService.findNamesByIds(CommonUtils.splitByCommas(ids));
    }

}
