package com.raysi.springbootcrudapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class SpringBootCrudAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCrudAppApplication.class, args);
    }

}
