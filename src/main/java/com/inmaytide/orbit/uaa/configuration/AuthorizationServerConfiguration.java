package com.inmaytide.orbit.uaa.configuration;

import com.inmaytide.exception.web.servlet.DefaultHandlerExceptionResolver;
import com.inmaytide.orbit.commons.domain.OrbitClientDetails;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.uaa.configuration.oauth2.authentication.CustomizedOAuth2TokenIntrospectionAuthenticationProvider;
import com.inmaytide.orbit.uaa.configuration.oauth2.authentication.OAuth2ResourceOwnerPasswordAuthenticationConverter;
import com.inmaytide.orbit.uaa.configuration.oauth2.authentication.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * @author inmaytide
 * @since 2023/4/12
 */
@EnableWebSecurity
@DependsOn("exceptionResolver")
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfiguration {

    private final ApplicationProperties properties;

    private final DefaultHandlerExceptionResolver exceptionResolver;

    public AuthorizationServerConfiguration(ApplicationProperties properties, DefaultHandlerExceptionResolver exceptionResolver) {
        this.properties = properties;
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
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
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RegisteredClientRepository clientRepository, OAuth2AuthorizationService authorizationService) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.apply(authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> tokenEndpoint.accessTokenRequestConverter(
                new DelegatingAuthenticationConverter(Arrays.asList(
                        new OAuth2AuthorizationCodeAuthenticationConverter(),
                        new OAuth2RefreshTokenAuthenticationConverter(),
                        new OAuth2ClientCredentialsAuthenticationConverter(),
                        new OAuth2ResourceOwnerPasswordAuthenticationConverter()))
        )));
        authorizationServerConfigurer.tokenIntrospectionEndpoint(endpoint -> endpoint.authenticationProvider(new CustomizedOAuth2TokenIntrospectionAuthenticationProvider(clientRepository, authorizationService)));
        http.exceptionHandling()
                .accessDeniedHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex))
                .authenticationEntryPoint((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex));
        http.csrf().disable();
        http.formLogin().disable();
        http.authorizeHttpRequests()
                .requestMatchers("/oauth2/**").permitAll()
                .anyRequest().authenticated();
        http.apply(authorizationServerConfigurer);
        http.headers().httpStrictTransportSecurity().disable();
        http.authenticationProvider(new OAuth2ResourceOwnerPasswordAuthenticationProvider(http.getSharedObject(AuthenticationManager.class), authorizationService, properties));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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

