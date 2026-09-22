package com.abhi.employeemanagement.entity.dto;

import com.abhi.employeemanagement.entity.enums.EmployeeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        Long phoneNumber,
        String department,
        String designation,
        Double salary,
        LocalDate joiningDate,
        EmployeeStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
