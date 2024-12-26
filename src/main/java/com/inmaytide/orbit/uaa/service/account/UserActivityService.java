package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.account.UserActivity;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/4/29
 */
public interface UserActivityService extends BasicService<UserActivity> {

    void persist(List<UserActivity> entities);

}
