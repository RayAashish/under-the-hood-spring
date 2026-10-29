package com.raysi.springbootcore;

import com.raysi.springbootcore.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootAPplication contains
 * 1. SpringBootConfiguration
 * 2. EnableAutoConfiguration which implements
 * 3. ComponentScan
 * so, simply the spring boot has provided a single annotation for repeated work.
 */
@SpringBootApplication
public class SpringBootCoreApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(SpringBootCoreApplication.class, args);
        var orderService = context.getBean(OrderService.class);
        orderService.order();
    }

}
