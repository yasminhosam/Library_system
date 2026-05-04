package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // INSERT
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    // READ
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // READ BY ID
    public Student getStudentById(int id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    // UPDATE
    public Student updateStudent(int id, Student studentDetails) {

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found to update"));


        existingStudent.setFirstname(studentDetails.getFirstname());
        existingStudent.setLastname(studentDetails.getLastname());
        existingStudent.setEmail(studentDetails.getEmail());
        existingStudent.setMaxBorrowLimit(studentDetails.getMaxBorrowLimit());



        return studentRepository.save(existingStudent);
    }

    // DELETE
    public String deleteStudent(int id) {
        if (!studentRepository.existsById(id)) {
            return "Student not found";
        }
        studentRepository.deleteById(id);
        return "Deleted Successfully.";
    }
}