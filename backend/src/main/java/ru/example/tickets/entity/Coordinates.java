package ru.example.tickets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import ru.example.tickets.enums.*;

@Entity
@Table(name = "coordinates")
@Getter
@Setter
public class Coordinates {
    @Id
    @SequenceGenerator(
            name = "coordinates_ids",
            sequenceName = "coordinates_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coordinates_ids")
    @Positive
    private Long id;

    @NotNull
    @DecimalMax("156")
    @Column(nullable = false)
    private Float x;

    @Column(nullable = false)
    private int y;

    @AssertTrue(message = "Координаты должны быть конечными числами")
    public boolean isFiniteCoordinates() {
        return x == null || Float.isFinite(x);
    }
}
