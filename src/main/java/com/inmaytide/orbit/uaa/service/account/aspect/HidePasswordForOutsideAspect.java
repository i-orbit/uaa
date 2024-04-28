package com.inmaytide.orbit.uaa.service.account.aspect;

import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.uaa.domain.account.User;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/4/28
 */
@Aspect
@Component
public class HidePasswordForOutsideAspect {

    @Pointcut("execution(* com.inmaytide.orbit.uaa.api.UserResource.*(..))")
    public void pointcut() {
    }

    @AfterReturning(value = "pointcut()", returning = "returnVal")
    public void onSuccess(Object returnVal) throws Throwable {
        if (returnVal instanceof User user) {
            user.setPassword(Constants.Markers.NOT_APPLICABLE);
        } else if (returnVal instanceof List<?> users) {
            if (CollectionUtils.isNotEmpty(users)) {
                users.stream()
                        .filter(e -> e instanceof User)
                        .map(e -> (User) e)
                        .forEach(e -> e.setPassword(Constants.Markers.NOT_APPLICABLE));
            }
        }

    }

}
