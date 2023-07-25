package com.inmaytide.orbit.uaa.api;

import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.domain.dto.result.PageResult;
import com.inmaytide.orbit.commons.log.annotation.OperationLogging;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.uaa.domain.tenant.Tenant;
import com.inmaytide.orbit.uaa.domain.tenant.TenantQuery;
import com.inmaytide.orbit.uaa.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@RestController
@Tag(name = "租户信息")
@RequestMapping("/api/tenants")
public class TenantResource {

    private final TenantService service;

    public TenantResource(TenantService service) {
        this.service = service;
    }

    @PostMapping
    @OperationLogging
    @Operation(summary = "创建租户信息")
    public Tenant create(@RequestBody @Validated Tenant tenant) {
        return service.create(tenant);
    }

    @PutMapping
    @OperationLogging
    @Operation(summary = "修改租户基本信息(名称/别名/LOGO图片)接口", description = "仅允许超级管理员和该租户的租户管理员修改")
    public Tenant update(@RequestBody @Validated Tenant tenant) {
        if (SecurityUtils.isSuperAdministrator()
                || SecurityUtils.isRobot()
                || SecurityUtils.isTenantAdministrator(tenant.getId())) {
            return service.update(tenant);
        }
        throw new AccessDeniedException();
    }

    @GetMapping("current")
    @Operation(summary = "查询当前登录用户的租户信息")
    public Tenant getWithCurrentUser() {
        return get(SecurityUtils.getAuthorizedUser().getTenantId());
    }

    @GetMapping("names-by-ids")
    @Operation(summary = "查询指定租户的名称")
    public Map<Long, String> getNamesByIds(@Parameter(description = "指定租户的ID,多个英文逗号隔开") @RequestParam String ids) {
        return service.getNamesByIds(ids);
    }

    @OperationLogging
    @GetMapping("{id}")
    @Operation(summary = "查询指定租户详细信息")
    public Tenant get(@PathVariable Long id) {
        return service.get(id).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(id)));
    }

    @OperationLogging
    @GetMapping
    @Operation(summary = "查询租户信息列表")
    public PageResult<Tenant> pagination(@ModelAttribute TenantQuery query) {
        return service.pagination(query);
    }


}
