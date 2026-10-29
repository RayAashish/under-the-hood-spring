package com.raysi.springbootcore.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public void pay(){
        IO.println("Payment Done!");
    }
}
