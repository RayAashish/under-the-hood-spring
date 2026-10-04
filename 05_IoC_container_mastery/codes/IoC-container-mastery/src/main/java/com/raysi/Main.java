package com.raysi;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        OrderService orderService = context.getBean(OrderService.class);
        orderService.placeOrder();
    }
}