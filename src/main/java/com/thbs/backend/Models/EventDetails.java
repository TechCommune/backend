package com.thbs.backend.Models;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
public class EventDetails {


    private String title;
    private String description;
    private String location;
    private String mode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxCapacity;
    private int price;
    private boolean paymentRequired;

    
}
