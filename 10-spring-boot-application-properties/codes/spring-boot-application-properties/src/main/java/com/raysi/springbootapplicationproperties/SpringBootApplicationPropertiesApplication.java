package com.raysi.springbootapplicationproperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootApplicationPropertiesApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootApplicationPropertiesApplication.class, args);
        var paymentGateway = context.getBean(PaymentGateway.class);
        System.out.println(paymentGateway.getType());
        System.out.println(paymentGateway.getRetryCount());

    }

}
