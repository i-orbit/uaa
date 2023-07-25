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
 * @since 2023/7/21
 */
@RestController
@Tag(name = "角色权限管理")
@RequestMapping("/api/roles")
public class RoleResource {

    private final RoleService service;

    public RoleResource(RoleService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "新增角色权限")
    public Role create(@RequestBody @Validated(Add.class) Role entity) {
        return service.create(entity);
    }

}
