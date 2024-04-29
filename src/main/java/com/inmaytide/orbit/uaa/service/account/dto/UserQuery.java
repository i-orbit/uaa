package com.inmaytide.orbit.uaa.service.account.dto;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.params.Pageable;
import com.inmaytide.orbit.uaa.domain.account.User;
import org.apache.commons.lang3.StringUtils;

/**
 * @author inmaytide
 * @since 2024/4/28
 */
public class UserQuery extends Pageable<User> {

    @Override
    public LambdaQueryWrapper<User> toWrapper() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.isNoneBlank(getQueryName()), w -> {
            w.like(User::getName, getQueryName())
                    .or().like(User::getTelephoneNumber, getPageNumber());
        });
        return new LambdaQueryWrapper<>();
    }

}
