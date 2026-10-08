package com.raysi.singletone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton") //By default it's singleton (Eager Fetching)
public class OrderService {

    @Autowired
    private PaymentService paymentService;

    public OrderService(){
        System.out.println("OrderService created");
    }


    public void placeOrder(){
        paymentService.pay();
        System.out.println("Order placed");
    }
}
