package com.raysi.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class OrderService {

    @Autowired
    private PaymentService paymentService;
    public OrderService(){
        System.out.println("OrderService Created");
    }


    public void order(){
        paymentService.pay();
        System.out.println("Order Placed");
    }
}
