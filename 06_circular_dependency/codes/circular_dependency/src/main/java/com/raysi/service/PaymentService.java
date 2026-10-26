package com.raysi.service;

import org.springframework.stereotype.Component;

/**
 * We can resolve circular dependency(CD) using Field injection & Setter Injection
 * But CD is extremely avoided because it's not a good practice
 * Here in this example, it's breaking the Single Responsibility principal
 * because the payment work shouldn't be to call order details, it's OrderService's work.
 */
@Component
public class PaymentService {

//    private final OrderService orderService;
//
//    public PaymentService(OrderService orderService){
//        this.orderService = orderService;
//    }

    public void pay(){
        System.out.println("Payment done");
//        orderService.getOrderDetails();
    }
}
