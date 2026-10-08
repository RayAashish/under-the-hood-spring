package com.raysi.prototype;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Course {

    public Course(){
        System.out.println("Course Created");
    }

    public void courseName(){
        System.out.println("Course");
    }
}
