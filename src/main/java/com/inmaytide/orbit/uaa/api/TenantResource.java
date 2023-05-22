package com.inmaytide.orbit.uaa.api;

import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.consts.Roles;
import com.inmaytide.orbit.commons.domain.GlobalUser;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.uaa.domain.Tenant;
import com.inmaytide.orbit.uaa.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@RestController
@Tag(name = "租户信息管理")
@RequestMapping("/api/tenants")
public class TenantResource {

    private final TenantService service;

    public TenantResource(TenantService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "创建租户信息")
    public Tenant create(@RequestBody @Validated Tenant tenant) {
        return service.create(tenant);
    }

    @PutMapping
    @Operation(summary = "租户管理员修改租户信息接口-仅支持修改名称/别名/LOGO信息", method = "PUT")
    public Tenant update(@RequestBody @Validated Tenant tenant) {
        GlobalUser operator = SecurityUtils.getAuthorizedUser();
        if (operator.getRoles().contains(Roles.ROLE_S_ADMINISTRATOR.name())
                || operator.getRoles().contains(Roles.ROLE_ROBOT.name())
                || (operator.getRoles().contains(Roles.ROLE_T_ADMINISTRATOR.name()) && Objects.equals(tenant.getId(), operator.getTenantId()))) {
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

    @GetMapping("{id}")
    @Operation(summary = "查询指定租户详细信息")
    public Tenant get(@PathVariable Long id) {
        return service.get(id).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(id)));
    }


}
