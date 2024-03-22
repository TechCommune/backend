package com.thbs.backend.Repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

import com.thbs.backend.Models.OTPModel;

public interface OtpRepo extends JpaRepository<OTPModel,UUID>{

    OTPModel findByEmail(String email);

    List<OTPModel> findByCreatedAt(LocalDateTime createdAt);
    
}