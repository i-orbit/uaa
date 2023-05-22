package com.inmaytide.orbit.uaa.configuration;

import com.inmaytide.exception.web.servlet.DefaultHandlerExceptionResolver;
import com.inmaytide.orbit.commons.consts.Roles;
import com.inmaytide.orbit.commons.domain.OrbitClientDetails;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.commons.security.CustomizedOpaqueTokenIntrospector;
import com.inmaytide.orbit.uaa.configuration.oauth2.authentication.CustomizedOAuth2TokenIntrospectionAuthenticationProvider;
import com.inmaytide.orbit.uaa.configuration.oauth2.authentication.OAuth2ResourceOwnerPasswordAuthenticationConverter;
import com.inmaytide.orbit.uaa.configuration.oauth2.authentication.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import com.inmaytide.orbit.uaa.configuration.oauth2.service.DefaultUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
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
@EnableWebSecurity
@EnableMethodSecurity
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
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("authorizationStore")
    public RedisTemplate<String, OAuth2Authorization> authorizationStore(RedisConnectionFactory factory) {
        RedisTemplate<String, OAuth2Authorization> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean("accessTokenStore")
    public RedisTemplate<String, OAuth2AccessToken> accessTokenStore(RedisConnectionFactory factory) {
        RedisTemplate<String, OAuth2AccessToken> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public AuthenticationManager authenticationManager(DefaultUserDetailsService userDetailService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    private OAuth2AuthorizationServerConfigurer authorizationServerConfigurer(RegisteredClientRepository clientRepository,
                                                                              OAuth2AuthorizationService authorizationService,
                                                                              AuthenticationManager authenticationManager) {
        return new OAuth2AuthorizationServerConfigurer()
                .tokenEndpoint((endpoint) -> {
                    endpoint.accessTokenRequestConverter(new OAuth2ResourceOwnerPasswordAuthenticationConverter());
                    endpoint.errorResponseHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
                    endpoint.authenticationProvider(new OAuth2ResourceOwnerPasswordAuthenticationProvider(authenticationManager, authorizationService, properties));
                }).tokenIntrospectionEndpoint(endpoint -> {
                    endpoint.authenticationProvider(new CustomizedOAuth2TokenIntrospectionAuthenticationProvider(clientRepository, authorizationService));
                });
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                          RegisteredClientRepository clientRepository,
                                                          OAuth2AuthorizationService authorizationService,
                                                          AuthenticationManager authenticationManager) throws Exception {
        http.apply(authorizationServerConfigurer(clientRepository, authorizationService, authenticationManager));
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.headers(c -> c.httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable));
        http.exceptionHandling(c -> {
            c.accessDeniedHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
            c.authenticationEntryPoint((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
            c.accessDeniedHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
        });
        http.authorizeHttpRequests(c -> {
            // 所有oauth2相关不需要登录
            c.requestMatchers("/oauth2/**").permitAll();
            // 租户管理员可以调用修改租户基本信息接口, 且在代码中验证只能修改自己租户的信息
            c.requestMatchers(HttpMethod.PUT, "/api/tenants").hasAnyAuthority(Roles.ROLE_S_ADMINISTRATOR.name(), Roles.ROLE_T_ADMINISTRATOR.name());
            // 剩余所有接口需要登录
            c.anyRequest().authenticated();
        });
        http.oauth2ResourceServer(c -> {
            c.authenticationEntryPoint((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
            c.accessDeniedHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
            c.opaqueToken(ot -> ot.introspector(new CustomizedOpaqueTokenIntrospector(restTemplate)));
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
                .clientId(Robot.getInstance().getUsername())
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

