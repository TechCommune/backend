package com.thbs.backend.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thbs.backend.Models.AdminModel;
   
public interface AdminRepo extends JpaRepository <AdminModel,UUID>{
    AdminModel findByEmail(String email);
    
   
} 
