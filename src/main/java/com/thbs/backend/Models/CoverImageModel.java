package com.thbs.backend.Models;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "cover_image_model", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"eventId"})
})
public class CoverImageModel {
    @Id
    private UUID coverImageId;

    @Column(length = 400)
    private String imageURL;

    private UUID organizerId;

    private UUID eventId;

    private LocalDate dateOfGenration;
}
