package com.raysi.service;

public class SmsNotificationService implements NotificationService{
    @Override
    public void sendNotification() {
        System.out.println("SMS notification sent............");
    }
}
