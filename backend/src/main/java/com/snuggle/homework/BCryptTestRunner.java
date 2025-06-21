package com.snuggle.homework;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// @Component
public class BCryptTestRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        String raw = "";
        String hashed = new BCryptPasswordEncoder().encode(raw);
        System.out.println("BCrypt 해시: " + hashed);
    }
}

