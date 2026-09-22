package ru.example.tickets.dto.view;

import ru.example.tickets.enums.EventType;

public record EventView(Long id, String name, long ticketsCount, EventType eventType) {}
