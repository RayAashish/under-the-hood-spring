package com.raysi.service;

/**
 * DI makes the Unit testing a lot easier actually
 * In real world application, we don't have to send the actual notification to check
 * if the OrderService is working or not, we can do that by creating the fake class
 */
public class FakeEmailNotificationService implements NotificationService{
    @Override
    public void sendNotification() {
        System.out.println("Fake email notification sent..........");
    }
}
