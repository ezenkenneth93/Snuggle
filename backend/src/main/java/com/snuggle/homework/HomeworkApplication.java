package com.snuggle.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.snuggle.homework")
public class HomeworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeworkApplication.class, args);
	}

	// @Autowired
    // private PasswordEncoder passwordEncoder;

	// @PostConstruct
    // public void printHash() {
    //     System.out.println("ADMIN 비밀번호 해시: " + passwordEncoder.encode("romazing@Q"));
    // }

}
