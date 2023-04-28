package com.inmaytide.orbit.uaa.configuration;

import com.inmaytide.orbit.commons.configuration.CommonProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author inmaytide
 * @since 2023/4/12
 */
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties extends CommonProperties {
}
