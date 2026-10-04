package com.raysi.payment;

import org.springframework.stereotype.Component;


/**
 * here we are having two implementation of PaymentService class
 * i.e. CardPaymentService & UPIPaymentService which both are Components
 * Now our IoC container is confused that for which one it should create
 * a Bean to inject in the CustomerService class
 * So, to resolve this, we have 2 things to do :
 * 1. @Primary annotation : If the IoC doesn't get any bean implicitly, it will
 * inject the class with Primary annotation
 * 2. @Qualifier annotation : Here we can mention multiple classes with @Qualifer
 * & we need to pass this while creating the dependency injection.
 */
public interface PaymentService {
    void doPayment();
}
