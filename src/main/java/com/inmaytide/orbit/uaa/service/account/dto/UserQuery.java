package com.inmaytide.orbit.uaa.service.account.dto;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.params.Pageable;
import com.inmaytide.orbit.uaa.domain.account.User;

/**
 * @author inmaytide
 * @since 2024/4/28
 */
public class UserQuery extends Pageable<User> {

    @Override
    public LambdaQueryWrapper<User> toWrapper() {
        return new LambdaQueryWrapper<>();
    }

}
