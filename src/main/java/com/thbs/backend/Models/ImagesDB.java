package com.thbs.backend.Models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ImagesDB {
    @Id
    private UUID imageId;

    @Column(length = 400)
    private String imageURL;

    // @ManyToOne
    // @JoinColumn(name = "organizer_id")
    private UUID organizerId;
}
