package ru.example.tickets.dto.request;

import jakarta.validation.constraints.NotNull;

public record LocationRequest(
        @NotNull Float x, @NotNull Integer y, @NotNull Float z, @NotNull String name) {}
