package com.abhi.employeemanagement.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmployeeKafkaConsumer {

    @KafkaListener(
            topics = "employee-events",
            groupId = "notification-group"
    )public void consume(EmployeeEvent event){
        System.out.println("Received Employee event : "+event);
    }
}
