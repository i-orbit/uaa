package com.inmaytide.orbit.uaa.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.commons.domain.GlobalUser;
import com.inmaytide.orbit.uaa.domain.User;
import com.inmaytide.orbit.uaa.mapper.UserMapper;
import com.inmaytide.orbit.uaa.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;

    public UserServiceImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public BaseMapper<User> getMapper() {
        return mapper;
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }


    @Override
    public GlobalUser findUserByUsername(String username) {
        return null;
    }

    @Override
    public User create(User entity) {
        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public void setExtraAttributes(Collection<User> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
    }

}
