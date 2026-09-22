package com.abhi.employeemanagement.kafka;

public record EmployeeEvent(
        Integer employeeId,
        String name,
        String eventType
) {
}
