package com.raysi.prototype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Student {
    @Autowired
    private Course course;

    public Student(){
        System.out.println("Student Bean Created");
    }
    public void details(){
        course.courseName();
        System.out.println("Other details");
    }
}
