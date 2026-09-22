package ru.example.tickets.dto.view;

import java.time.LocalDateTime;
import ru.example.tickets.enums.TicketType;

public record TicketView(
        long id,
        String name,
        CoordinatesView coordinates,
        LocalDateTime creationDate,
        PersonView person,
        EventView event,
        int price,
        TicketType type,
        long discount,
        Long number,
        VenueView venue) {}
