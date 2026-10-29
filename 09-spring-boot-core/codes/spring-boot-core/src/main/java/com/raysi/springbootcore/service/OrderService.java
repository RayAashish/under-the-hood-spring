package com.raysi.springbootcore.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    public void order(){
        paymentService.pay();
        IO.println("Order Placed....");
    }
}
