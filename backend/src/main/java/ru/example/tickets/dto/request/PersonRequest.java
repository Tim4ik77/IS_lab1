package ru.example.tickets.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.example.tickets.enums.Color;

public record PersonRequest(
        Color eyeColor,
        Color hairColor,
        @NotNull @Positive Long locationId,
        @Positive Long weight) {}
