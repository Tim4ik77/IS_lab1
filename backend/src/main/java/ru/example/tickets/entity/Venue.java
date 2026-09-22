package ru.example.tickets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import ru.example.tickets.enums.*;

@Entity
@Table(name = "venue")
@Getter
@Setter
public class Venue {
    @Id
    @SequenceGenerator(name = "venue_ids", sequenceName = "venue_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "venue_ids")
    @Positive
    private int id;

    @NotNull
    @Size(min = 1)
    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Positive

    private Long capacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VenueType type;
}
