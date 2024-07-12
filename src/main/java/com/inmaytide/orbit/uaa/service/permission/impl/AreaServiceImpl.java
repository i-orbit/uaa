package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.uaa.domain.permission.Area;
import com.inmaytide.orbit.uaa.mapper.permission.AreaMapper;
import com.inmaytide.orbit.uaa.service.permission.AreaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/7/12
 */
@Service
public class AreaServiceImpl implements AreaService {

    private final AreaMapper baseMapper;

    public AreaServiceImpl(AreaMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public List<Long> findAuthorizedIds(SystemUser user) {
        return List.of();
    }

    @Override
    public BaseMapper<Area> getBaseMapper() {
        return baseMapper;
    }
}
