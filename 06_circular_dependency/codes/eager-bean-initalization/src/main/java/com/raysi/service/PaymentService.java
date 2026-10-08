package com.raysi.service;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class PaymentService {

    public PaymentService(){
        System.out.println("PaymentService Created");
    }

    public void pay(){
        System.out.println("Payment done");
    }
}
