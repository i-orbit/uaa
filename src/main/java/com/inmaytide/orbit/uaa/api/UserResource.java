package com.inmaytide.orbit.uaa.api;

import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.domain.dto.AffectedResult;
import com.inmaytide.orbit.commons.domain.dto.PageResult;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.User;
import com.inmaytide.orbit.uaa.domain.dto.UserQuery;
import com.inmaytide.orbit.uaa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@RestController
@Api(tags = "用户信息管理接口")
@RequestMapping("/api/users")
public class UserResource {

    private final UserService service;

    public UserResource(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ApiOperation("新增用户信息")
    public User create(@RequestBody @Validated User user) {
        return service.create(user);
    }

    @PutMapping
    @ApiOperation("修改用户信息")
    public User update(@RequestBody @Validated User user) {
        return service.update(user);
    }

    @DeleteMapping
    @ApiOperation("删除指定用户信息")
    public AffectedResult deleteByIds(@ApiParam("指定用户唯一标识, 多个用英文逗号隔开") @RequestParam String ids) {
        return service.deleteByIds(CommonUtils.splitToLongByCommas(ids));
    }

    @GetMapping
    @ApiOperation("分页查询用户信息列表")
    public PageResult<User> pagination(@ModelAttribute UserQuery params) {
        return service.pagination(params);
    }

    @GetMapping("{id}")
    @ApiOperation("查询指定用户信息")
    public User get(@PathVariable Long id) {
        return service.get(id).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(id)));
    }

}
