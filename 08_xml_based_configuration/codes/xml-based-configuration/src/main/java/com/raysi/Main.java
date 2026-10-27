package com.raysi;

import com.raysi.service.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args){
        ApplicationContext context =
                new ClassPathXmlApplicationContext("beans.xml");

        OrderService orderService = context.getBean(OrderService.class);
//        orderService.order();
    }
}
