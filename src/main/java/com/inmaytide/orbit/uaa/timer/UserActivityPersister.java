package com.inmaytide.orbit.uaa.timer;

import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.constants.Platforms;
import com.inmaytide.orbit.commons.metrics.AbstractJob;
import com.inmaytide.orbit.commons.metrics.JobParameter;
import com.inmaytide.orbit.commons.utils.ValueCaches;
import com.inmaytide.orbit.uaa.domain.account.UserActivity;
import com.inmaytide.orbit.uaa.service.account.UserActivityService;
import jakarta.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author inmaytide
 * @since 2024/4/29
 */
public class UserActivityPersister extends AbstractJob {

    private static final Logger log = LoggerFactory.getLogger(UserActivityPersister.class);

    private static final JobParameter parameter = JobParameter.withName("persister_user-activity")
            .active()
            .cronExpression("0 * * * * ?")
            .reinitializeIfExistingAtServiceStartup()
            .build();

    private UserActivityService userActivityService;

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public String getName() {
        return parameter.getName();
    }

    @NonNull
    @Override
    protected JobParameter getParameters() {
        return parameter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void exec(JobExecutionContext context) {
        List<UserActivity> entities = ValueCaches.keys(Constants.CacheNames.USER_ACTIVITY).stream()
                .map(k -> ValueCaches.getFor(Constants.CacheNames.USER_ACTIVITY, k, UserActivity.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(e -> Duration.between(e.getLastActivityTime(), Instant.now()).getSeconds() >= 300)
                .toList();
        if (!entities.isEmpty()) {
            entities.forEach(e -> e.setOfflineTime(e.getLastActivityTime()));
            userActivityService.saveBatch(entities);
            entities.stream()
                    .map(e -> getUserActivityCacheKey(e.getPlatform(), e.getUser()))
                    .forEach(k -> ValueCaches.delete(Constants.CacheNames.USER_ACTIVITY, k));
        }
    }

    private String getUserActivityCacheKey(Platforms platform, Long userId) {
        return platform.name() + "::" + userId;
    }


    @Resource
    public void setUserActivityService(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }
}
