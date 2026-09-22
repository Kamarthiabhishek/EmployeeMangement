package com.abhi.employeemanagement.service;

import com.abhi.employeemanagement.entity.Employee;
import com.abhi.employeemanagement.entity.dto.EmployeeRequest;
import com.abhi.employeemanagement.entity.dto.EmployeeResponse;
import com.abhi.employeemanagement.entity.enums.EmployeeStatus;
import com.abhi.employeemanagement.exception.EmployeeAlreadyExists;
import com.abhi.employeemanagement.exception.EmployeeDoesntExists;
import com.abhi.employeemanagement.kafka.EmployeeEvent;
import com.abhi.employeemanagement.kafka.EmployeeKafkaProducer;
import com.abhi.employeemanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeKafkaProducer kafkaProducer;
    public EmployeeService(EmployeeRepository employeeRepository,  EmployeeKafkaProducer kafkaProducer){
        this.employeeRepository = employeeRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public EmployeeResponse buildEmployeeResponse(Employee employee){
        return new EmployeeResponse(
            employee.getId(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail(),
            employee.getPhoneNumber(),
            employee.getDepartment(),
            employee.getDesignation(),
            employee.getSalary(),
            employee.getJoiningDate(),
            employee.getStatus(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public EmployeeResponse createEmployee(EmployeeRequest request){
        if(employeeRepository.existsByEmail(request.email())){
            throw new EmployeeAlreadyExists("Employee Already Exists with Email : " +request.email());
        }
        Employee employee = new Employee(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.department(),
                request.designation(),
                request.salary(),
                request.joiningDate(),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Employee createdEmployee = employeeRepository.save(employee);
        EmployeeEvent event = new EmployeeEvent(
               createdEmployee.getId(),
               createdEmployee.getFirstName(),
               "EMPLOYEE_CREATED"
        );
        kafkaProducer.sendEmployeeCreated(event);
        return buildEmployeeResponse(createdEmployee);
    }

    public EmployeeResponse getEmployee(Integer id){
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeDoesntExists("Employee Doesn't Exists for ID : "+id));
        return buildEmployeeResponse(employee);
    }

    public List<EmployeeResponse> getEmployees(){
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(this::buildEmployeeResponse).toList();
    }

    public String deleteEmployee(Integer id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeDoesntExists(
                                "Employee Doesn't Exists for ID : " + id
                        )
                );
        employee.setStatus(EmployeeStatus.REMOVED);
        employee.setUpdatedDate(LocalDateTime.now());

        Employee savedEmployee = employeeRepository.save(employee);
        EmployeeEvent event = new EmployeeEvent(
                savedEmployee.getId(),
                savedEmployee.getFirstName(),
                "EMPLOYEE_DELETED"
        );
        kafkaProducer.sendEmployeeDeleted(event);
        return "Employee Successfully deleted for ID : " + id;
    }
    public EmployeeResponse updateEmployee(Integer id){
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeDoesntExists("Employee doesn't exists with ID : "+id));
        employee.setStatus(EmployeeStatus.ON_LEAVE);
        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeEvent event = new EmployeeEvent(
                savedEmployee.getId(),
                savedEmployee.getFirstName(),
                "EMPLOYEE_UPDATED"
        );
        kafkaProducer.sendEmployeeUpdated(event);
        return buildEmployeeResponse(savedEmployee);
    }
}