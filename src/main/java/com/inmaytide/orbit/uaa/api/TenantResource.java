package com.inmaytide.orbit.uaa.api;

import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.domain.dto.params.Pageable;
import com.inmaytide.orbit.commons.domain.dto.result.PageResult;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.uaa.domain.permission.Tenant;
import com.inmaytide.orbit.uaa.service.permission.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author inmaytide
 * @since 2024/7/4
 */
@Tag(name = "租户信息", description = "除查询当前用户自己的租户信息外, 其他接口仅系统超级管理员可用")
@RestController
@RequestMapping("/api/tenants")
public class TenantResource {

    private final TenantService tenantService;

    public TenantResource(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    @Operation(summary = "分页查询租户信息")
    public PageResult<Tenant> pagination(@ModelAttribute Pageable<Tenant> pageable) {
        return tenantService.pagination(pageable);
    }

    @PostMapping
    @Operation(summary = "创建租户")
    public Tenant create(@Validated @RequestBody Tenant tenant) {
        return tenantService.create(tenant);
    }

    @PutMapping
    @Operation(summary = "修改租户")
    public Tenant update(@Validated @RequestBody Tenant tenant) {
        return tenantService.create(tenant);
    }

    @GetMapping("{id}")
    @Operation(summary = "查询指定租户信息", description = "除系统超级管理员外其他用户仅可查看自己所属租户的详细信息")
    public Tenant get(@PathVariable Long id) {
        Tenant tenant = tenantService.get(id).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(id)));
        if (SecurityUtils.isSuperAdministrator()) {
            return tenant;
        }
        if (Objects.equals(SecurityUtils.getAuthorizedUser().getTenant(), tenant.getId())) {
            return tenant;
        }
        throw new AccessDeniedException();
    }

}
