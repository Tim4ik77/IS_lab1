package ru.example.tickets.dto.view;

import ru.example.tickets.enums.Color;

public record PersonView(
        Long id, Color eyeColor, Color hairColor, LocationView location, Long weight) {}
