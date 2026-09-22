package ru.example.tickets.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SellRequest(
        @NotNull @Positive Long ticketId,
        @NotNull @Positive Long personId,
        @NotNull @Positive Integer amount) {}
