package com.javalab.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.javalab.student")
public class WithmeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WithmeBackendApplication.class, args);
    }
}
