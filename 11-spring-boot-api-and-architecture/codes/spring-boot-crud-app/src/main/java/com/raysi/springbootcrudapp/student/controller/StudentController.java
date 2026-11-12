package com.raysi.springbootcrudapp.student.controller;

import com.raysi.springbootcrudapp.student.entity.Student;
import com.raysi.springbootcrudapp.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping("/api/students")
    public List<Student> getStudents(){
        return studentService.getAllStudents();
    }

    @PostMapping("/api/students")
    public String saveStudents(@RequestBody List<Student> students){
        studentService.saveAllStudents(students);
        return "OK";
    }

    @DeleteMapping("/api/{id}")
    public String deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return "Deleted Successfully";
    }
}
