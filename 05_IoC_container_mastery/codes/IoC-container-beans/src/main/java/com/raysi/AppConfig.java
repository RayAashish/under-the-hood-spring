package com.raysi;

import com.raysi.entity.User;
import com.raysi.payment.CardPaymentService;
import com.raysi.payment.PaymentService;
import com.raysi.payment.UPIPaymentService;
import com.raysi.service.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Many times we might see where there are no way to create a class as Component
 * suppose we are using a different model of our system which can't be edited
 * so we can't mark it as @Component so in that case @Bean comes into play
 * we can simply invert the control for DI to the IoC rather than controlling
 * ourselves by marking the Object as @Bean.
 */


@Configuration
@ComponentScan("com.raysi")
public class AppConfig {


    @Bean
    public User user(){
        return new User(24, "Aashish");
    }

    /**
     * Suppose our class isn't marked Component, still we can automate
     * it by marking it as a bean method.
     * Now IoC container will create a bean of PaymentService with
     * @return CardPaymentService
     */
    @Bean
    public PaymentService paymentService(){
        return new CardPaymentService();
    }

    @Bean
    @Primary
    public PaymentService paymentServiceByUPI(){
        return new UPIPaymentService();
    }

    @Bean
    public CustomerService customerService(PaymentService paymentService){
        return new CustomerService(paymentService);
    }
}

