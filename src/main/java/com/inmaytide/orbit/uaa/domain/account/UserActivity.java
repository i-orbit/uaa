package com.inmaytide.orbit.uaa.domain.account;

import com.inmaytide.orbit.commons.constants.Platforms;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author inmaytide
 * @since 2024/4/23
 */
public class UserActivity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Platforms platform;

    private final Instant onlineTime;

    private String ipAddress;

    private Instant lastActivityTime;

    public UserActivity() {
        this.onlineTime = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
