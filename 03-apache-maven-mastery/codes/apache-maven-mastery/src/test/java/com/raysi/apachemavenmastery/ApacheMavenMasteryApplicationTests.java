package com.raysi.apachemavenmastery;

import com.raysi.apachemavenmastery.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApacheMavenMasteryApplicationTests {

    @Autowired
    HomeController controller;

    @Test
    void contextLoads() {
    }

    @Test
    void showHome() {
        String str = controller.home();
        System.out.println(str);
    }

}
