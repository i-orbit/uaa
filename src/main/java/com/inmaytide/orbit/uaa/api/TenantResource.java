package com.inmaytide.orbit.uaa.api;

import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.uaa.domain.Tenant;
import com.inmaytide.orbit.uaa.service.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@RestController
@Api(tags = "租户信息管理")
@RequestMapping("/api/tenants")
public class TenantResource {

    private final TenantService service;

    public TenantResource(TenantService service) {
        this.service = service;
    }

    @PostMapping
    @ApiOperation("创建租户信息")
    public Tenant create(@RequestBody @Validated Tenant tenant) {
        return service.create(tenant);
    }

    @PutMapping
    @ApiOperation("租户管理员修改租户信息接口-仅支持修改名称/别名/LOGO信息")
    public Tenant update(@RequestBody @Validated Tenant tenant) {
        return service.update(tenant);
    }

    @GetMapping("{id}")
    @ApiOperation("查询指定租户详细信息")
    public Tenant get(@PathVariable Long id) {
        return service.get(id).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(id)));
    }


}
