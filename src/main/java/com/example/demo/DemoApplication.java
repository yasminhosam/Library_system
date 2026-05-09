package com.example.demo;

import com.example.demo.service.StudentService;
import com.example.demo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    private StudentService studentService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}