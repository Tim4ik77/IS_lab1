package ru.example.tickets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import ru.example.tickets.enums.*;

@Entity
@Table(name = "person")
@Getter
@Setter
public class Person {
    @Id
    @SequenceGenerator(name = "person_ids", sequenceName = "person_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_ids")
    @Positive
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color")
    private Color eyeColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color")
    private Color hairColor;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Positive

    private Long weight;
}
