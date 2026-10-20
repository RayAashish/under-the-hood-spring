package com.raysi;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderService {
    Map<Integer, String> map;
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService){
        this.paymentService = paymentService;
        System.out.println("OrderService Bean is Created!");
    }

    @PostConstruct
    public void loadData(){
        System.out.println("PostConstruct Initalized");
        map = new HashMap<>();
        map.put(1, "Aashish");
        map.put(2, "Bhanu");
    }

    public void order(){
        paymentService.pay();
        System.out.println("Order Paid for : " + map.get(1));

    }
}
