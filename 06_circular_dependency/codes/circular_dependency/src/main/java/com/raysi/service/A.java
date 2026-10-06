package com.raysi.service;

import org.springframework.stereotype.Component;

@Component
public class A {
    private final B b;

    public A(B b){
        this.b = b;
    }

    public void doSomething(){
        b.doSomething();
        System.out.println("Something from A");
    }
}
