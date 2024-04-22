package com.thbs.backend.Models;

import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;



@Entity
@Component
@Data
@Table(name="attendee_list" )
public class AttendeeList {
    


    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    private UUID eventId;

    @NotNull
    private String userName;

    @NotNull
    private String email;


    
}


