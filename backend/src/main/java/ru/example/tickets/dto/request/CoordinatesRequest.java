package ru.example.tickets.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;

public record CoordinatesRequest(@NotNull @DecimalMax("156") Float x, @NotNull Integer y) {}
