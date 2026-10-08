package com.raysi.singletone.service;

import org.springframework.stereotype.Component;

@Component
public class PaymentService {

    public PaymentService(){
        System.out.println("Payment service created");
    }

    public void pay(){
        System.out.println("Payment done");
    }
}
