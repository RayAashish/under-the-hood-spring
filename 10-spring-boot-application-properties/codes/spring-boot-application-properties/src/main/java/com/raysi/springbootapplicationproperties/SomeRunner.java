package com.raysi.springbootapplicationproperties;

import com.raysi.springbootapplicationproperties.paymentgateway.PaymentGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SomeRunner implements CommandLineRunner {
    private final PaymentGateway paymentGateway;

    public SomeRunner(PaymentGateway paymentGateway){
        this.paymentGateway = paymentGateway;
    }
    @Override
    public void run(String... args) throws Exception {
        paymentGateway.pay();
    }
}
