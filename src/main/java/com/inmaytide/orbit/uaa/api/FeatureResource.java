package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.uaa.domain.feature.Feature;
import com.inmaytide.orbit.uaa.service.feature.FeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.TreeSet;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
@RestController
@RequestMapping("/api/features")
@Tag(name = "系统功能模块管理", description = "除查询当前用户租户功能模块权限接口外, 其他接口仅系统超级管理员可用")
public class FeatureResource {

    private final FeatureService featureService;

    public FeatureResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GetMapping("tree-of-features")
    public TreeSet<TreeNode<Object>> treeOfFeatures(@RequestParam(required = false) Bool includeMenus) {
        return featureService.treeOfFeatures(includeMenus);
    }

    @PostMapping
    @Operation(summary = "添加系统功能模块")
    public Feature create(@RequestBody @Validated Feature entity) {
        return featureService.create(entity);
    }

    @PutMapping
    @Operation(summary = "添加系统功能模块")
    public Feature update(@RequestBody @Validated Feature entity) {
        return featureService.update(entity);
    }

}
