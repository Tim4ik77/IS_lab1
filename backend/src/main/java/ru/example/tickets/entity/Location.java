package ru.example.tickets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import ru.example.tickets.enums.*;

@Entity
@Table(name = "location")
@Getter
@Setter
public class Location {
    @Id
    @SequenceGenerator(name = "location_ids", sequenceName = "location_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_ids")
    @Positive
    private Long id;

    @Column(nullable = false)
    private float x;

    @NotNull
    @Column(nullable = false)
    private Integer y;

    @Column(nullable = false)
    private float z;

    @NotNull
    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @AssertTrue(message = "Координаты должны быть конечными числами")
    public boolean isFiniteCoordinates() {
        return Float.isFinite(x) && Float.isFinite(z);
    }
}
