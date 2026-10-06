package ru.example.tickets.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record LocationRequest(
        @NotNull Float x,
        @NotNull Integer y,
        @NotNull Float z,
        @NotNull String name) {
    @AssertTrue(message = "Координаты должны быть конечными числами")
    public boolean isFiniteCoordinates() {
        return (x == null || Float.isFinite(x)) && (z == null || Float.isFinite(z));
    }
}
