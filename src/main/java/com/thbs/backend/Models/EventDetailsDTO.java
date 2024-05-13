package com.thbs.backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventDetailsDTO {
    private Event event;
    private int maxCapacity;
}