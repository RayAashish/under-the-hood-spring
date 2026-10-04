package com.raysi.service;

import com.raysi.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerService {
    private final PaymentService paymentService;

    @Autowired
    public CustomerService(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    public void order(){
        paymentService.doPayment();
        System.out.println("Order placed");
    }
}
