package com.rayfay.gira.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + encodedPassword);

        // 验证密码
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Password matches: " + matches);

        // 验证数据库中的密码
        String dbPassword = "$2a$10$rAYxqUqP.1nqZgYFZiPOxuS8OMxhvHzH.Y2.Qh0qKqvq4YuNJa5Uy";
        boolean dbMatches = encoder.matches(rawPassword, dbPassword);
        System.out.println("DB password matches: " + dbMatches);
    }
}