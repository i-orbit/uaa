package com.inmaytide.orbit.uaa.configuration.oauth2.service;

import com.inmaytide.exception.web.BadCredentialsException;
import com.inmaytide.orbit.commons.consts.Marks;
import com.inmaytide.orbit.commons.consts.UserState;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.service.AuthorityService;
import com.inmaytide.orbit.uaa.service.role.RoleService;
import com.inmaytide.orbit.uaa.service.user.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author inmaytide
 * @since 2023/04/29
 */
@Component
public class DefaultUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityService authorityService;

    private final RoleService roleService;

    public DefaultUserDetailsService(UserService userService, PasswordEncoder passwordEncoder, AuthorityService authorityService, RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authorityService = authorityService;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean withoutPassword = username.endsWith(Marks.LOGIN_WITHOUT_PASSWORD.getValue());
        final String loginName = withoutPassword ? username.replaceAll(Marks.LOGIN_WITHOUT_PASSWORD.getValue(), "") : username;
        User user = userService.findUserByUsername(loginName).orElseThrow(() -> new BadCredentialsException(ErrorCode.E_0x00100002, loginName));
        return org.springframework.security.core.userdetails.User.withUsername(String.valueOf(user.getId()))
                .password(withoutPassword ? passwordEncoder.encode(Marks.LOGIN_WITHOUT_PASSWORD.getValue()) : user.getPassword())
                .accountLocked(user.getState() == UserState.LOCKED)
                .disabled(user.getState() == UserState.DISABLED)
                .authorities(createAuthoritiesWithUser(user))
                .build();
    }

    private List<GrantedAuthority> createAuthoritiesWithUser(User user) {
        return Stream.concat(authorityService.findAuthoritiesByUser(user).stream(), roleService.findRoleCodesByUser(user).stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
