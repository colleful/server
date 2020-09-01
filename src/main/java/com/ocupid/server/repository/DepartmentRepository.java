package com.ocupid.server.repository;

import com.ocupid.server.domain.Department;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCollegeAndDepartment(String college, String department);
}
