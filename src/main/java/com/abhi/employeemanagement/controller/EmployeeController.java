package com.abhi.employeemanagement.controller;

import com.abhi.employeemanagement.entity.dto.EmployeeRequest;
import com.abhi.employeemanagement.entity.dto.EmployeeResponse;
import com.abhi.employeemanagement.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping("")
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.createEmployee(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Integer id){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployee(id));
    }
    @GetMapping("")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployees());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Integer id){
        return ResponseEntity.ok(employeeService.deleteEmployee(id));
    }

}
