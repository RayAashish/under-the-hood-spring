package com.raysi.service;

import org.springframework.stereotype.Component;

@Component
public class B {
    private final A a;

    public B(A a){
        this.a = a;
    }

    public void doSomething(){
        a.doSomething();
        System.out.println("Something done by B");
    }
}
