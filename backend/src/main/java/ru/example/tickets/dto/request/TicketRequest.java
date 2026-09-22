package ru.example.tickets.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.example.tickets.enums.TicketType;

public record TicketRequest(
        @NotNull @Size(min = 1) String name,
        @NotNull @Positive Long coordinatesId,
        @Positive Long personId,
        @NotNull @Positive Long eventId,
        @NotNull @Positive Integer price,
        TicketType type,
        @NotNull @Min(1) @Max(100) Long discount,
        @Positive Long number,
        @NotNull @Positive Integer venueId) {}
