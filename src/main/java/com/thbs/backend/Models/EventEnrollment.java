package com.thbs.backend.Models;

import java.sql.Date;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EventEnrollment {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID enrollId;

    private UUID userId;

    private UUID eventId;

    private Date enrollmentDate;

    private int price;

    private boolean paymentRequired;
}
