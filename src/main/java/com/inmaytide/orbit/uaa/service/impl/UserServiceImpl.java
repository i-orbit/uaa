package com.inmaytide.orbit.uaa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.consts.CacheNames;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.domain.GlobalUser;
import com.inmaytide.orbit.uaa.domain.User;
import com.inmaytide.orbit.uaa.mapper.UserMapper;
import com.inmaytide.orbit.uaa.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Primary
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
    public User findUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, Is.N.name());
        wrapper.or(w ->
                w.eq(User::getUsername, username)
                        .or().eq(User::getTelephoneNumber, username)
                        .or().eq(User::getEmail, username)
                        .or().eq(User::getEmployeeId, username)
        );
        return mapper.selectOne(wrapper);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.USER_DETAILS, key = "#id", unless = "#result == null")
    public GlobalUser loadUserById(Serializable id) {
        User user = mapper.selectById(id);

        GlobalUser globalUser = new GlobalUser();
        BeanUtils.copyProperties(user, globalUser);
//        globalUser.setRoles();
//        globalUser.setAuthorities();
        return globalUser;
    }

    @Override
    public User create(User entity) {
        getMapper().insert(entity);
        updated();
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(entity.getId())));
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.USER_DETAILS, key = "#entity.id")
    public User update(User entity) {
        getMapper().updateById(entity);
        updated();
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(entity.getId())));
    }

    @Override
    public void setExtraAttributes(Collection<User> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
    }

}
