package com.raysi;


import com.raysi.prototype.Student;
import com.raysi.singletone.service.OrderService;
import com.raysi.singletone.service.PaymentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
//        OrderService orderService = context.getBean(OrderService.class);
//        orderService.placeOrder();
//
//        Student student = context.getBean(Student.class);
//        student.details();


    }
}