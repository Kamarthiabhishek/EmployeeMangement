package com.abhi.employeemanagement.repository;

import com.abhi.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Boolean existsByEmail(String email);
    Optional<Employee> findById(int id);
}
