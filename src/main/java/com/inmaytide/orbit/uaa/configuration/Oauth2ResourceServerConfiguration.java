package com.inmaytide.orbit.uaa.configuration;

import com.inmaytide.exception.web.servlet.DefaultHandlerExceptionResolver;
import com.inmaytide.orbit.commons.constants.Roles;
import com.inmaytide.orbit.commons.security.CustomizedBearerTokenResolver;
import com.inmaytide.orbit.commons.security.CustomizedOpaqueTokenIntrospector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

/**
 * @author inmaytide
 * @since 2023/4/12
 */
@DependsOn("exceptionResolver")
@Configuration(proxyBeanMethods = false)
public class Oauth2ResourceServerConfiguration {

    private final DefaultHandlerExceptionResolver exceptionResolver;

    private final RestTemplate restTemplate;

    public Oauth2ResourceServerConfiguration(DefaultHandlerExceptionResolver exceptionResolver, RestTemplate restTemplate) {
        this.exceptionResolver = exceptionResolver;
        this.restTemplate = restTemplate;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(c -> {
            // 不需要登录可直接访问
            // API docs
            c.requestMatchers("/v3/api-docs/**").permitAll();
            c.requestMatchers("/swagger-ui/**").permitAll();
            c.requestMatchers(HttpMethod.POST, "/api/authorize").permitAll();
            // 自助重置密码
            c.requestMatchers(HttpMethod.POST, "/api/users/passwords/apply-verification-code").permitAll();
            c.requestMatchers(HttpMethod.PUT, "/api/users/passwords/change-with-validation-code").permitAll();
            // 系统后台管理相关功能-除个别外需要超级管理员角色权限
            c.requestMatchers("/api/tenants/{id}").authenticated();
            c.requestMatchers("/api/features/tree-of-features").authenticated();
            c.requestMatchers("/api/tenants").hasAuthority(Roles.ROLE_S_ADMINISTRATOR.name());
            c.requestMatchers("/api/features").hasAuthority(Roles.ROLE_S_ADMINISTRATOR.name());
            // 剩余所有接口需要登录
            c.anyRequest().authenticated();
        });
        http.oauth2ResourceServer(c -> {
            c.authenticationEntryPoint((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
            c.accessDeniedHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
            c.bearerTokenResolver(new CustomizedBearerTokenResolver());
            c.opaqueToken(ot -> ot.introspector(new CustomizedOpaqueTokenIntrospector(restTemplate)));
        });
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.headers(c -> c.httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable));
        http.exceptionHandling(c -> {
            c.accessDeniedHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
            c.authenticationEntryPoint((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
        });
        return http.build();
    }

}

