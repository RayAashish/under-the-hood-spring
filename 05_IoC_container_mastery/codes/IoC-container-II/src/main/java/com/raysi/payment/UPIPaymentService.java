package com.raysi.payment;

import org.springframework.stereotype.Component;

@Component
public class UPIPaymentService implements PaymentService{
    @Override
    public void doPayment() {
        System.out.println("UPI payment done");
    }
}
