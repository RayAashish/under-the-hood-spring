package com.raysi.payment;

import org.springframework.stereotype.Component;

//@Component
public class CardPaymentService implements PaymentService{
    @Override
    public void doPayment() {
        System.out.println("Card payment done");
    }
}
