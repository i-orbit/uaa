package com.inmaytide.orbit.uaa.service.account.impl;

import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.uaa.domain.account.UserActivity;
import com.inmaytide.orbit.uaa.mapper.account.UserActivityMapper;
import com.inmaytide.orbit.uaa.service.account.UserActivityService;
import org.springframework.stereotype.Service;

/**
 * @author inmaytide
 * @since 2024/4/29
 */
@Service
public class UserActivityServiceImpl extends BasicServiceImpl<UserActivityMapper, UserActivity> implements UserActivityService {
}
