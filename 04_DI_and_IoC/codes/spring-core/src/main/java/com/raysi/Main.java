package com.raysi;

import com.raysi.service.EmailNotificationService;
import com.raysi.service.NotificationService;

/**
 * @author Aashish K. Ray
 * The game of SOLID principle's
 * We don't want to make our program tightly coupled at all because in bigger projects,
 * there are thousands of classes & making it tightly coupled will result in changing the code
 * from hundreds of place which we don't want.
 *
 * So for that reason, we inject the dependency to some other class
 * Here, we have used the Main class but when we will use spring application,
 * the injection will be done through Spring Core --Dependency Injection (DI)
 */
public class Main {
    public static void main(String[] args) {
        NotificationService notificationService = new EmailNotificationService();
        OrderService service = new OrderService(notificationService);
        service.placeOrder();
    }
}