package com.colleful.server.department.service;

import com.colleful.server.department.domain.Department;
import java.util.List;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartment(Long id);
}
