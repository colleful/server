package com.ocupid.server.service;

import com.ocupid.server.domain.Department;
import com.ocupid.server.repository.DepartmentRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartment(Long id) {
        return departmentRepository.findById(id);
    }
}
