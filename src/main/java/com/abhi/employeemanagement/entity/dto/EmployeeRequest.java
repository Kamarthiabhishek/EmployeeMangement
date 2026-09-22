package com.abhi.employeemanagement.entity.dto;

import java.time.LocalDate;

public record EmployeeRequest(
        String firstName,
        String lastName,
        String email,
        Long phoneNumber,
        String department,
        String designation,
        Double salary,
        LocalDate joiningDate
) {
}
