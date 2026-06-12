package com.findra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FindraApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindraApplication.class, args);
    }
}
