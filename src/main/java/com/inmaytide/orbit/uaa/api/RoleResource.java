package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.commons.domain.validation.groups.Add;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import com.inmaytide.orbit.uaa.service.permission.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author inmaytide
 * @since 2024/7/1
 */
@Tag(name = "用户角色")
@RestController
@RequestMapping("/api/roles")
public class RoleResource {

    private final RoleService service;

    public RoleResource(RoleService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "新增角色信息")
    public Role create(@Validated(Add.class) @RequestBody Role entity) {
        return service.create(entity);
    }

    @PostMapping
    @Operation(summary = "修改角色信息")
    public Role update(@Validated(Add.class) @RequestBody Role entity) {
        return service.create(entity);
    }

}
