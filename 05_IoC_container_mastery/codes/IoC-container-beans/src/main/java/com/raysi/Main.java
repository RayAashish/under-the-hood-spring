package com.raysi;


import com.raysi.entity.User;
import com.raysi.payment.CardPaymentService;
import com.raysi.service.CustomerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        User user = context.getBean(User.class);
        System.out.println(user.getName());

        CustomerService customerService = context.getBean(CustomerService.class);
        customerService.order();
    }
}