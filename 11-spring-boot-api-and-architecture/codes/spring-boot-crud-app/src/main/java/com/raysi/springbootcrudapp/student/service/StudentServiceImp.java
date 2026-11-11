package com.raysi.springbootcrudapp.student.service;

import com.raysi.springbootcrudapp.student.entity.Student;
import com.raysi.springbootcrudapp.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImp implements StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentServiceImp(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public void saveAllStudents(List<Student> students) {
        studentRepository.saveAll(students);
    }
}
