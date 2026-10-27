package com.raysi.service;

import org.springframework.beans.factory.annotation.Autowired;

public class OrderService {

    private final PaymentService paymentService;

//    @Autowired
    public OrderService(PaymentService paymentService){
        this.paymentService = paymentService;
        System.out.println("OrderService created");

    }

    public void order(){

    }

}
