package ru.example.tickets.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.example.tickets.enums.VenueType;

public record VenueRequest(
        @NotNull @Size(min = 1) String name,
        @Positive Long capacity,
        @NotNull VenueType type) {}
