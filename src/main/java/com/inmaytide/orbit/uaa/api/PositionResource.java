package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.domain.validation.groups.Add;
import com.inmaytide.orbit.commons.domain.validation.groups.Update;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.account.Position;
import com.inmaytide.orbit.uaa.service.account.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author inmaytide
 * @since 2024/5/31
 */
@Tag(name = "岗位管理")
@RestController
@RequestMapping("/api/positions")
public class PositionResource {

    private final PositionService positionService;

    public PositionResource(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    @Operation(summary = "新增岗位信息")
    public Position create(@Validated(Add.class) @RequestBody Position entity) {
        return positionService.create(entity);
    }

    @PutMapping
    @Operation(summary = "修改岗位信息")
    public Position update(@Validated(Update.class) @RequestBody Position entity) {
        return positionService.update(entity);
    }

    @DeleteMapping
    @Operation(summary = "删除指定岗位信息")
    public AffectedResult deleteByIds(@RequestParam String ids) {
        return positionService.deleteByIds(CommonUtils.splitByCommas(ids));
    }

    @GetMapping("tree-of-positions")
    @Operation(summary = "查询当前用户有权限的组织架构+岗位架构树", description = "包含部分组成树结构时必要的无权限组织")
    public TreeSet<TreeNode<? extends Entity>> treeOfPositions() {
        return positionService.treeOfPositions();
    }

    @GetMapping("names")
    @Operation(summary = "查询指定岗位名称")
    public Map<String, String> findNamesByIds(@RequestParam String ids) {
        return positionService.findNamesByIds(CommonUtils.splitByCommas(ids));
    }

}
