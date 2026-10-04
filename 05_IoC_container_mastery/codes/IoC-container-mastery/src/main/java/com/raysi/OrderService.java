package com.raysi;

import com.raysi.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public OrderService(PaymentService service){
        this.paymentService = service;
    }

    public void placeOrder(){
        paymentService.doPayment();
        System.out.println("Order placed");
    }
}
