package com.raysi.springbootcrudapp.student.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String welcome(){
        return """
                <h1> Hello, Spring! </h1>
                """;
    }
}
