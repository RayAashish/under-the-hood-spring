package com.raysi.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


public class CardPaymentService implements PaymentService{
    @Override
    public void doPayment() {
        System.out.println("Card payment done");
    }
}
