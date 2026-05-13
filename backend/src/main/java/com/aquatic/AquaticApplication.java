package com.aquatic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aquatic.mapper")
public class AquaticApplication {
    public static void main(String[] args) {
        SpringApplication.run(AquaticApplication.class, args);
    }
}
