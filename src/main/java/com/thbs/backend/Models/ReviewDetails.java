package com.thbs.backend.Models;

import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ReviewDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID reviewId;

    private UUID eventId;

    private String userName;

    private UUID userId;

    private String review;

}