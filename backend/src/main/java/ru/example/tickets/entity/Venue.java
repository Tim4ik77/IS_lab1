package ru.example.tickets.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.example.tickets.enums.VenueType;

@Entity
@Table(name = "venue")
@Getter
@Setter
public class Venue {
    @Id
    @SequenceGenerator(name = "venue_ids", sequenceName = "venue_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "venue_ids")
    @Positive
    private Integer id;

    @NotNull
    @Size(min = 1)
    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Positive private Long capacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VenueType type;
}
