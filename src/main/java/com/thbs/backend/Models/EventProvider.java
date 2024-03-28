package com.thbs.backend.Models;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Component
@Data
@Entity
@Table(name="event_organizer")
public class EventProvider {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "organizer_id")
    private UUID id;

    @NotNull
    private String userName;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String orgName;

    @NotNull
    private long phoneNumber;


}
