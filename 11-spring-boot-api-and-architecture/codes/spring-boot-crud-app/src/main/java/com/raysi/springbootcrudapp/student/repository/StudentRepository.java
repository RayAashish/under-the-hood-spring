package com.raysi.springbootcrudapp.student.repository;

import com.raysi.springbootcrudapp.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
