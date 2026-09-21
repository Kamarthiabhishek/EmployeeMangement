package com.abhi.employeemanagement.exception;

public class EmployeeDoesntExists extends RuntimeException {
  public EmployeeDoesntExists(String message) {
    super(message);
  }
}
