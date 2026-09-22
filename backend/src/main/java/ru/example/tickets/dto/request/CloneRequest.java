package ru.example.tickets.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CloneRequest(
        @NotNull @Positive Long ticketId, @NotNull @Min(1) @Max(100) Long discount) {}
