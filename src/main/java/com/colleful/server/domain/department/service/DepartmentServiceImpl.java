package com.colleful.server.domain.department.service;

import com.colleful.server.domain.department.domain.Department;
import com.colleful.server.domain.department.repository.DepartmentRepository;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartment(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("학과 정보가 없습니다."));
    }
}
