package com.abhi.employeemanagement.config;

import com.abhi.employeemanagement.entity.dto.ErrorResponse;
import com.abhi.employeemanagement.exception.EmployeeAlreadyExists;
import com.abhi.employeemanagement.exception.EmployeeDoesntExists;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(
             Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(EmployeeAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleEmployeeAlreadyExists(EmployeeAlreadyExists ex , HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(),request);
    }

    @ExceptionHandler(EmployeeDoesntExists.class)
    public ResponseEntity<ErrorResponse> handleEmployeeDoesntExists(EmployeeDoesntExists ex,  HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }
}
