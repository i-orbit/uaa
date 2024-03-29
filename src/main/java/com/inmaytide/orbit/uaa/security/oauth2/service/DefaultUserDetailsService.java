package com.inmaytide.orbit.uaa.security.oauth2.service;

import com.inmaytide.exception.web.BadCredentialsException;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.constants.UserState;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.service.account.UserService;
import com.inmaytide.orbit.uaa.service.permission.AuthorityService;
import com.inmaytide.orbit.uaa.service.permission.RoleService;
import org.apache.commons.lang3.StringUtils;
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

    public static final String ROLE_PREFIX = "ROLE_";

    private final UserService userService;

    private final RoleService roleService;

    private final AuthorityService authorityService;

    private final PasswordEncoder passwordEncoder;

    public DefaultUserDetailsService(UserService userService, RoleService roleService, AuthorityService authorityService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.authorityService = authorityService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean withoutPassword = username.endsWith(Constants.Markers.LOGIN_WITHOUT_PASSWORD);
        final String loginName = withoutPassword ? StringUtils.removeEnd(username, Constants.Markers.LOGIN_WITHOUT_PASSWORD) : username;
        User user = userService.findByLoginName(loginName).orElseThrow(() -> new BadCredentialsException(ErrorCode.E_0x00100002, loginName));
        return org.springframework.security.core.userdetails.User.withUsername(String.valueOf(user.getId()))
                .password(withoutPassword ? passwordEncoder.encode(Constants.Markers.LOGIN_WITHOUT_PASSWORD) : user.getPassword())
                .accountLocked(user.getState() == UserState.LOCKED)
                .disabled(user.getState() == UserState.DISABLED)
                .authorities(createAuthoritiesWithUser(user))
                .build();
    }

    private List<GrantedAuthority> createAuthoritiesWithUser(User user) {
        List<String> roleCodes = roleService.findCodesByUser(user.getId());
        List<String> authorityCodes = authorityService.findCodesByRoleCodes(roleCodes);
        return Stream.concat(roleCodes.stream().map(e -> !e.startsWith(ROLE_PREFIX) ? ROLE_PREFIX + e : e), authorityCodes.stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
