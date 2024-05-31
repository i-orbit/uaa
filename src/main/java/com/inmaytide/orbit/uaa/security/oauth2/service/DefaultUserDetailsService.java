package com.inmaytide.orbit.uaa.security.oauth2.service;

import com.inmaytide.exception.web.BadCredentialsException;
import com.inmaytide.orbit.commons.business.SystemUserService;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.constants.UserState;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.service.account.UserService;
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

    private final PasswordEncoder passwordEncoder;

    private final SystemUserService systemUserService;

    public DefaultUserDetailsService(UserService userService, PasswordEncoder passwordEncoder, SystemUserService systemUserService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.systemUserService = systemUserService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean withoutPassword = username.endsWith(Constants.Markers.LOGIN_WITHOUT_PASSWORD);
        final String loginName = withoutPassword ? StringUtils.removeEnd(username, Constants.Markers.LOGIN_WITHOUT_PASSWORD) : username;
        User user = userService.findByLoginName(loginName).orElseThrow(() -> new BadCredentialsException(ErrorCode.E_0x00100002, loginName));
        SystemUser systemUser = systemUserService.get(user.getId());
        return org.springframework.security.core.userdetails.User.withUsername(String.valueOf(user.getId()))
                .password(withoutPassword ? passwordEncoder.encode(Constants.Markers.LOGIN_WITHOUT_PASSWORD) : user.getPassword())
                .accountLocked(user.getState() == UserState.LOCKED)
                .disabled(user.getState() == UserState.DISABLED)
                .authorities(createAuthoritiesWithUser(systemUser))
                .build();
    }

    private List<GrantedAuthority> createAuthoritiesWithUser(SystemUser user) {
        return Stream.concat(user.getPermission().getRoles().stream().map(e -> !e.startsWith(ROLE_PREFIX) ? ROLE_PREFIX + e : e), user.getPermission().getAuthorities().stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
