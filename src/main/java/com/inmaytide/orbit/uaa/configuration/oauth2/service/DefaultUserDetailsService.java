package com.inmaytide.orbit.uaa.configuration.oauth2.service;

import com.inmaytide.orbit.commons.consts.Marks;
import com.inmaytide.orbit.commons.consts.UserState;
import com.inmaytide.orbit.uaa.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author luomiao
 * @since 2020/11/10
 */
@Component
public class DefaultUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(DefaultUserDetailsService.class);

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public DefaultUserDetailsService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean withoutPassword = username.endsWith(Marks.LOGIN_WITHOUT_PASSWORD.getValue());
        if (withoutPassword) {
            username = username.replaceAll(Marks.LOGIN_WITHOUT_PASSWORD.getValue(), "");
        }
        com.inmaytide.orbit.uaa.domain.User user = userService.findUserByUsername(username);
        return User.withUsername(user.getUsername())
                .password(withoutPassword ? passwordEncoder.encode(Marks.LOGIN_WITHOUT_PASSWORD.getValue()) : user.getPassword())
                .accountLocked(user.getState() == UserState.LOCKED)
                .disabled(user.getState() == UserState.DISABLED)
                .authorities(createAuthoritiesWithUser(user))
                .build();
    }


    public List<GrantedAuthority> createAuthoritiesWithUser(com.inmaytide.orbit.uaa.domain.User user) {
        return Collections.emptyList();

//        return Stream.concat(user.getAuthorities().stream(), user.getRoles().stream().map(e -> Constants.Roles.PREFIX_AUTHORITY_ROLE_TYPE + e))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
    }
}
