package ru.example.tickets.dto.view;

import ru.example.tickets.enums.VenueType;

public record VenueView(int id, String name, Long capacity, VenueType type) {}
