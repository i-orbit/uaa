package com.inmaytide.orbit.uaa;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author inmaytide
 * @since 2023/4/27
 */
public class Tester {

    public static void main(String... args) {
        System.out.println(new BCryptPasswordEncoder().encode("111111"));
    }

}
