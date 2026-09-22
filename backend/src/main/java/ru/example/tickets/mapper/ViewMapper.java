package ru.example.tickets.mapper;

import ru.example.tickets.dto.view.*;

import ru.example.tickets.entity.*;

public final class ViewMapper {
    private ViewMapper() {}

    public static CoordinatesView of(Coordinates c) {
        return new CoordinatesView(c.getId(), c.getX(), c.getY());
    }

    public static LocationView of(Location l) {
        return new LocationView(l.getId(), l.getX(), l.getY(), l.getZ(), l.getName());
    }

    public static PersonView of(Person p) {
        return p == null
                ? null
                : new PersonView(
                        p.getId(),
                        p.getEyeColor(),
                        p.getHairColor(),
                        of(p.getLocation()),
                        p.getWeight());
    }

    public static EventView of(Event e) {
        return new EventView(e.getId(), e.getName(), e.getTicketsCount(), e.getEventType());
    }

    public static VenueView of(Venue v) {
        return new VenueView(v.getId(), v.getName(), v.getCapacity(), v.getType());
    }

    public static TicketView of(Ticket t) {
        return new TicketView(
                t.getId(),
                t.getName(),
                of(t.getCoordinates()),
                t.getCreationDate(),
                of(t.getPerson()),
                of(t.getEvent()),
                t.getPrice(),
                t.getType(),
                t.getDiscount(),
                t.getNumber(),
                of(t.getVenue()));
    }

}
