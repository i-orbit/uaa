package com.inmaytide.orbit.uaa;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author inmaytide
 * @since 2023/7/7
 */
public class PasswordEncoderTester {

    public static void main(String... args) {
        System.out.println(new BCryptPasswordEncoder().encode("Ssgm888888"));
    }

}
