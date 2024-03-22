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
@Table(name="user_info" )
public class UserModel {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;

    
}
