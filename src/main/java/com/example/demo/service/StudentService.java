package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // INSERT
    public void addStudent(Student s) {
        studentRepository.save(s);
    }

    // READ
    public void getAllStudents() {
        studentRepository.findAll().forEach(s ->
                System.out.println(s.getFirstname() + " " + s.getLastname())
        );
    }

    // UPDATE
    public void updateStudent(int id) {
        Student s = studentRepository.findById(id).orElse(null);
        if (s != null) {
            s.setFirstname("Updated");
            studentRepository.save(s);
        }
    }

    // DELETE
    public void deleteStudent(int id) {
        studentRepository.deleteById(id);
    }
}