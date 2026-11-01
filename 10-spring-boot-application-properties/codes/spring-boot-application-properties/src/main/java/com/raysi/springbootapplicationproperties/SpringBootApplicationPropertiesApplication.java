package com.raysi.springbootapplicationproperties;

import com.raysi.springbootapplicationproperties.paymentgateway.PaymentGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootApplicationPropertiesApplication {

    public static void main(String[] args) {
        /* ApplicationContext context = */ SpringApplication.run(SpringBootApplicationPropertiesApplication.class, args);
//        var paymentGateway = context.getBean(PaymentGateway.class);
//
//        paymentGateway.pay();
    }

}
