package com.thbs.backend.Models;


import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Entity;
import org.springframework.stereotype.Component;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
@Component
public class Event {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "event_id")
    private UUID id;

    @NotNull
    private UUID event_org_id;

    @NotNull
    private String title;

    private String description;
    
    @NotNull
    private String location;

    @NotNull
    private String mode;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int price;

    private boolean paymentRequired;
  
}