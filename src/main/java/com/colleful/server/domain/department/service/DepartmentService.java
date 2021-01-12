package com.colleful.server.domain.department.service;

import com.colleful.server.domain.department.domain.Department;
import java.util.List;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartment(Long id);
}
