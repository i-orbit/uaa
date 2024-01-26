package com.inmaytide.orbit.uaa.configuration;

import com.inmaytide.orbit.commons.configuration.GlobalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author inmaytide
 * @since 2023/4/12
 */
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties extends GlobalProperties {

    public boolean allowUsersToLoginSimultaneously;

    public boolean isAllowUsersToLoginSimultaneously() {
        return allowUsersToLoginSimultaneously;
    }

    public void setAllowUsersToLoginSimultaneously(boolean allowUsersToLoginSimultaneously) {
        this.allowUsersToLoginSimultaneously = allowUsersToLoginSimultaneously;
    }
}
