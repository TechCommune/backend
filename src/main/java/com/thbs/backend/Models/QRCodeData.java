package com.thbs.backend.Models;

import java.util.UUID;

public class QRCodeData {
    private UUID eventId;
    private UUID userId;

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}