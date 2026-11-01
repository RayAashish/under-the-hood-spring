package com.raysi.springbootapplicationproperties.paymentgateway;

import com.raysi.springbootapplicationproperties.configuration.PaymentGatewayConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentGateway {
//
//    /**
//     * There are few things which should come from outside
//     * instead of hardcoding & Payment gateway is one example of that
//     * so, we can get it from the application.properties file
//     * Syntax : @Value("${property-name}")
//     */
//    @Value("${PaymentGateway.type}")
//    private String type;
//    @Value("${PaymentGateway.retry-count}")
//    private int retryCount;
//
//    public String getType() {
//
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public int getRetryCount() {
//        return retryCount;
//    }
//
//    public void setRetryCount(int retryCount) {
//        this.retryCount = retryCount;
//    }
    private final PaymentGatewayConfiguration paymentGatewayConfiguration;

    @Autowired
    public PaymentGateway(PaymentGatewayConfiguration paymentGatewayConfiguration){
        this.paymentGatewayConfiguration = paymentGatewayConfiguration;
    }

    public void pay(){
        System.out.println(paymentGatewayConfiguration.getType());
        System.out.println(paymentGatewayConfiguration.getRetryCount());
        System.out.println(paymentGatewayConfiguration.getTimeout());
        System.out.println(paymentGatewayConfiguration.isEnabled());
    }
}
