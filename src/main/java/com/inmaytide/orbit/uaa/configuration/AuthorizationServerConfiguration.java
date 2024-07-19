package com.inmaytide.orbit.uaa.configuration;

import com.inmaytide.exception.web.servlet.DefaultHandlerExceptionResolver;
import com.inmaytide.orbit.commons.constants.Roles;
import com.inmaytide.orbit.commons.domain.OrbitClientDetails;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.commons.security.CustomizedBearerTokenResolver;
import com.inmaytide.orbit.commons.security.CustomizedOpaqueTokenIntrospector;
import com.inmaytide.orbit.uaa.security.oauth2.authentication.CustomizedOAuth2TokenIntrospectionAuthenticationProvider;
import com.inmaytide.orbit.uaa.security.oauth2.authentication.CustomizedOAuth2TokenIntrospectionAuthenticationSuccessHandler;
import com.inmaytide.orbit.uaa.security.oauth2.authentication.OAuth2PasswordAuthenticationConverter;
import com.inmaytide.orbit.uaa.security.oauth2.authentication.OAuth2PasswordAuthenticationProvider;
import com.inmaytide.orbit.uaa.security.oauth2.service.DefaultUserDetailsService;
import com.inmaytide.orbit.uaa.security.oauth2.store.OAuth2AccessTokenStore;
import com.inmaytide.orbit.uaa.security.oauth2.store.OAuth2AuthorizationStore;
import com.inmaytide.orbit.uaa.security.oauth2.store.RedisOAuth2AccessTokenStore;
import com.inmaytide.orbit.uaa.security.oauth2.store.RedisOAuth2AuthorizationStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author inmaytide
 * @since 2023/4/12
 */
@DependsOn("exceptionResolver")
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfiguration {

    private final ApplicationProperties properties;

    private final DefaultHandlerExceptionResolver exceptionResolver;

    private final RestTemplate restTemplate;

    public AuthorizationServerConfiguration(ApplicationProperties properties, DefaultHandlerExceptionResolver exceptionResolver, RestTemplate restTemplate) {
        this.properties = properties;
        this.exceptionResolver = exceptionResolver;
        this.restTemplate = restTemplate;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizationStore.class)
    public OAuth2AuthorizationStore authorizationStore(RedisConnectionFactory connectionFactory) {
        return new RedisOAuth2AuthorizationStore(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2AccessTokenStore.class)
    public OAuth2AccessTokenStore accessTokenStore() {
        return new RedisOAuth2AccessTokenStore();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public AuthenticationManager authenticationManager(DefaultUserDetailsService userDetailService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      RegisteredClientRepository clientRepository,
                                                                      OAuth2AuthorizationService authorizationService,
                                                                      AuthenticationManager authenticationManager) throws Exception {
        final OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.with(authorizationServerConfigurer, c -> {
            c.tokenEndpoint((endpoint) -> {
                endpoint.errorResponseHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
                endpoint.accessTokenRequestConverter(new OAuth2PasswordAuthenticationConverter());
                endpoint.authenticationProvider(new OAuth2PasswordAuthenticationProvider(authenticationManager, authorizationService, properties));
            }).tokenIntrospectionEndpoint(endpoint -> {
                endpoint.errorResponseHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
                endpoint.authenticationProvider(new CustomizedOAuth2TokenIntrospectionAuthenticationProvider(clientRepository, authorizationService));
                endpoint.introspectionResponseHandler(new CustomizedOAuth2TokenIntrospectionAuthenticationSuccessHandler());
            }).clientAuthentication(customizer-> {
                customizer.errorResponseHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
            });
        });
        http.authorizeHttpRequests(c -> {
            // 不需要登录可直接访问
            // API docs
            c.requestMatchers("/v3/api-docs/**").permitAll();
            c.requestMatchers("/swagger-ui/**").permitAll();
            // Oauth2 endpoints
            c.requestMatchers(authorizationServerConfigurer.getEndpointsMatcher()).permitAll();
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

    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        return new InMemoryRegisteredClientRepository(orbit(passwordEncoder), robot(passwordEncoder));
    }


    private RegisteredClient orbit(PasswordEncoder passwordEncoder) {
        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                .accessTokenTimeToLive(Duration.of(OrbitClientDetails.getInstance().getAccessTokenValiditySeconds(), ChronoUnit.SECONDS))
                .refreshTokenTimeToLive(Duration.of(OrbitClientDetails.getInstance().getRefreshTokenValiditySeconds(), ChronoUnit.SECONDS))
                .idTokenSignatureAlgorithm(SignatureAlgorithm.ES256)
                .reuseRefreshTokens(true)
                .build();
        return RegisteredClient.withId(OrbitClientDetails.ORBIT_CLIENT_ID)
                .clientId(OrbitClientDetails.getInstance().getClientId())
                .clientSecret(passwordEncoder.encode(OrbitClientDetails.getInstance().getClientSecret()))
                .clientAuthenticationMethods(consumer -> {
                    consumer.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    consumer.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                })
                .authorizationGrantTypes(grantTypes -> OrbitClientDetails.getInstance().getAuthorizedGrantTypes().stream().map(AuthorizationGrantType::new).forEach(grantTypes::add))
                .scopes(scopes -> scopes.addAll(OrbitClientDetails.getInstance().getScopes()))
                .tokenSettings(tokenSettings)
                .build();
    }


    private RegisteredClient robot(PasswordEncoder passwordEncoder) {
        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                .accessTokenTimeToLive(Duration.of(60, ChronoUnit.SECONDS))
                .idTokenSignatureAlgorithm(SignatureAlgorithm.ES256)
                .reuseRefreshTokens(true)
                .build();
        return RegisteredClient.withId(Robot.ROBOT_CLIENT_ID)
                .clientId(Robot.getInstance().getLoginName())
                .clientSecret(passwordEncoder.encode(Robot.getInstance().getPassword()))
                .authorizationGrantType(new AuthorizationGrantType(Robot.ROBOT_GRANT_TYPE))
                .clientAuthenticationMethods(consumer -> {
                    consumer.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    consumer.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                })
                .scope("all")
                .tokenSettings(tokenSettings)
                .build();
    }

}

