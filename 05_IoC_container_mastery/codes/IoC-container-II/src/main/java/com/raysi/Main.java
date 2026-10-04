package com.raysi;


import com.raysi.payment.CardPaymentService;
import com.raysi.service.CustomerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        CustomerService service = context.getBean(CustomerService.class);
        service.order();
    }
}