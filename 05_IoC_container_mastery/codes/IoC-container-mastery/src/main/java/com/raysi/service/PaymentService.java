package com.raysi.service;


import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    public void doPayment(){
        System.out.println("Payment done");
    }
}
