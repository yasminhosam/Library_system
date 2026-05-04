package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department addDepartment(Department department) {

        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department updateDepartment(int id, Department details) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        existing.setDeptName(details.getDeptName());
        return departmentRepository.save(existing);
    }

    public String deleteDepartment(int id) {
        if (!departmentRepository.existsById(id)) {
            return "Department not found";
        }
        departmentRepository.deleteById(id);
        return "Deleted Successfully";
    }
}