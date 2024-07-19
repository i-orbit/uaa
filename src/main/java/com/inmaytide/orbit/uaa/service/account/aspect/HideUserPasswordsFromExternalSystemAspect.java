package com.inmaytide.orbit.uaa.service.account.aspect;

import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.domain.dto.result.PageResult;
import com.inmaytide.orbit.uaa.domain.account.User;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * When the {@link com.inmaytide.orbit.uaa.configuration.ApplicationProperties#hideUserPasswordsFromExternal} configuration is set to true,
 * intercept the user information interfaces provided to external systems
 * and automatically erase the user login passwords.
 *
 * @author inmaytide
 * @since 2024/4/28
 */
@Aspect
@Component
@ConditionalOnProperty(prefix = "application", name = "hide-user-passwords-from-external", havingValue = "true", matchIfMissing = true)
public class HideUserPasswordsFromExternalSystemAspect {

    @Pointcut("execution(* com.inmaytide.orbit.uaa.api.UserResource.*(..))")
    public void pointcut() {
    }

    @AfterReturning(value = "pointcut()", returning = "returnVal")
    public void onSuccess(Object returnVal) throws Throwable {
        if (returnVal instanceof User user) {
            eraseUserPassword(Collections.singleton(user));
        } else if (returnVal instanceof List<?> users) {
            eraseUserPassword(users);
        } else if (returnVal instanceof PageResult<?> res) {
            eraseUserPassword(res.getElements());
        }
    }

    private void eraseUserPassword(Collection<?> users) {
        if (CollectionUtils.isEmpty(users)) {
            return;
        }
        for (Object entity : users) {
            if (entity instanceof User user) {
                user.setPassword(Constants.Markers.NOT_APPLICABLE);
            }
        }
    }

}
