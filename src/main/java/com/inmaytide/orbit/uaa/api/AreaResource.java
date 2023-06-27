package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.Area;
import com.inmaytide.orbit.uaa.service.AreaService;
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
@Tag(name = "区域位置")
@RequestMapping("/api/areas")
public class AreaResource {

    private final AreaService service;

    public AreaResource(AreaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "新增区域位置")
    public Area create(@RequestBody @Validated Area entity) {
        return service.create(entity);
    }

    @PutMapping
    @Operation(summary = "修改区域位置")
    public Area update(@RequestBody @Validated Area entity) {
        return service.update(entity);
    }

    @DeleteMapping
    @Operation(summary = "删除指定区域位置")
    public AffectedResult deleteByIds(@Parameter(description = "指定区域的唯一标识, 多个英文逗号隔开", required = true) @RequestParam String ids) {
        return service.deleteByIds(CommonUtils.splitToLongByCommas(ids));
    }

    @GetMapping
    @Operation(summary = "查询区域位置树")
    public List<TreeNode<Area>> getTreeOfAreas() {
        return service.getTreeOfAreas();
    }

}
