package com.raysi;

import com.raysi.service.NotificationService;

public class OrderService {

    NotificationService notificationService;

    OrderService(NotificationService service){
        this.notificationService = service;
    }

    void placeOrder(){
        System.out.println("Order placed");
        notificationService.sendNotification();
    }
}
