package com.inmaytide.orbit.uaa.domain.account;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.constants.Platforms;
import com.inmaytide.orbit.commons.domain.pattern.Entity;

import java.io.Serial;
import java.time.Instant;

/**
 * @author inmaytide
 * @since 2024/4/23
 */
public class UserActivity extends Entity {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    private Long user;

    private Platforms platform;

    private final Instant onlineTime;

    private String ipAddress;

    private Instant lastActivityTime;

    public Instant offlineTime;

    public UserActivity() {
        this.onlineTime = Instant.now();
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Platforms getPlatform() {
        return platform;
    }

    public void setPlatform(Platforms platform) {
        this.platform = platform;
    }

    public Instant getOnlineTime() {
        return onlineTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(Instant lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public Instant getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Instant offlineTime) {
        this.offlineTime = offlineTime;
    }
}
