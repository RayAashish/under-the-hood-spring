package com.raysi.springbootcrudapp.student.service;

import com.raysi.springbootcrudapp.student.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentService {
    List<Student> getAllStudents();
    void saveAllStudents(List<Student> students);
    void deleteStudent(Long id);
}
