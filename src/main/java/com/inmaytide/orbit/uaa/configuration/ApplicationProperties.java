package com.inmaytide.orbit.uaa.configuration;

import com.inmaytide.orbit.commons.configuration.GlobalProperties;
import com.inmaytide.orbit.uaa.consts.UserDefaultPasswordMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author inmaytide
 * @since 2023/4/12
 */
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties extends GlobalProperties {

    private UserDefaultPassword userDefaultPassword;

    public boolean allowUsersToLoginSimultaneously;

    public boolean hideUserPasswordsFromExternal;

    public boolean isAllowUsersToLoginSimultaneously() {
        return allowUsersToLoginSimultaneously;
    }

    public void setAllowUsersToLoginSimultaneously(boolean allowUsersToLoginSimultaneously) {
        this.allowUsersToLoginSimultaneously = allowUsersToLoginSimultaneously;
    }

    public boolean isHideUserPasswordsFromExternal() {
        return hideUserPasswordsFromExternal;
    }

    public void setHideUserPasswordsFromExternal(boolean hideUserPasswordsFromExternal) {
        this.hideUserPasswordsFromExternal = hideUserPasswordsFromExternal;
    }

    public UserDefaultPassword getUserDefaultPassword() {
        return userDefaultPassword;
    }

    public void setUserDefaultPassword(UserDefaultPassword userDefaultPassword) {
        this.userDefaultPassword = userDefaultPassword;
    }

    public final static class UserDefaultPassword {

        private UserDefaultPasswordMode mode;

        private String specified;

        public UserDefaultPasswordMode getMode() {
            return mode;
        }

        public void setMode(UserDefaultPasswordMode mode) {
            this.mode = mode;
        }

        public String getSpecified() {
            return specified;
        }

        public void setSpecified(String specified) {
            this.specified = specified;
        }
    }

}
