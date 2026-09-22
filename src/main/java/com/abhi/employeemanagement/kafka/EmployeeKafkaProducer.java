package com.abhi.employeemanagement.kafka;

import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class EmployeeKafkaProducer {

    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;
    public EmployeeKafkaProducer(KafkaTemplate<String, EmployeeEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmployeeCreated(EmployeeEvent employeeEvent){
        kafkaTemplate.send("employee-events",employeeEvent.employeeId().toString(), employeeEvent);
    }
}
