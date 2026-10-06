package ru.example.tickets.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;

public record CoordinatesRequest(
        @NotNull @DecimalMax(value = "156", message = "Значение X должно быть не больше 156") Float x,
        @NotNull Integer y) {
    @AssertTrue(message = "Координаты должны быть конечными числами")
    public boolean isFiniteCoordinates() {
        return x == null || Float.isFinite(x);
    }
}
