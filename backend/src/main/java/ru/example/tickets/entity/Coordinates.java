package ru.example.tickets.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

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
    @DecimalMax(value = "156", message = "Значение X должно быть не больше 156")
    @Column(nullable = false)
    private Float x;

    @Column(nullable = false)
    private int y;

    @AssertTrue(message = "Координаты должны быть конечными числами")
    public boolean isFiniteCoordinates() {
        return x == null || Float.isFinite(x);
    }
}
