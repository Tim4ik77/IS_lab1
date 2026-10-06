package ru.example.tickets.mapper;

import ru.example.tickets.dto.view.CoordinatesView;
import ru.example.tickets.dto.view.EventView;
import ru.example.tickets.dto.view.LocationView;
import ru.example.tickets.dto.view.PersonView;
import ru.example.tickets.dto.view.TicketView;
import ru.example.tickets.dto.view.VenueView;
import ru.example.tickets.entity.Coordinates;
import ru.example.tickets.entity.Event;
import ru.example.tickets.entity.Location;
import ru.example.tickets.entity.Person;
import ru.example.tickets.entity.Ticket;
import ru.example.tickets.entity.Venue;

public final class ViewMapper {
    private ViewMapper() {}

    public static CoordinatesView of(Coordinates coordinates) {
        return new CoordinatesView(coordinates.getId(), coordinates.getX(), coordinates.getY());
    }

    public static LocationView of(Location location) {
        return new LocationView(
                location.getId(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getName());
    }

    public static PersonView of(Person person) {
        return person == null
                ? null
                : new PersonView(
                        person.getId(),
                        person.getEyeColor(),
                        person.getHairColor(),
                        of(person.getLocation()),
                        person.getWeight());
    }

    public static EventView of(Event event) {
        return new EventView(
                event.getId(), event.getName(), event.getTicketsCount(), event.getEventType());
    }

    public static VenueView of(Venue venue) {
        return new VenueView(venue.getId(), venue.getName(), venue.getCapacity(), venue.getType());
    }

    public static TicketView of(Ticket ticket) {
        return new TicketView(
                ticket.getId(),
                ticket.getName(),
                of(ticket.getCoordinates()),
                ticket.getCreationDate(),
                of(ticket.getPerson()),
                of(ticket.getEvent()),
                ticket.getPrice(),
                ticket.getType(),
                ticket.getDiscount(),
                ticket.getNumber(),
                of(ticket.getVenue()));
    }
}
