package ru.example.tickets.service;

import ru.example.tickets.dto.request.TicketRequest;
import ru.example.tickets.dto.view.PageView;
import ru.example.tickets.dto.view.TicketView;
import ru.example.tickets.mapper.ViewMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.example.tickets.entity.*;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.repository.*;

import java.util.Map;

@Service
@Transactional
public class TicketService {
    private final EntityRepository entities;
    private final TicketRepository tickets;

    public TicketService(EntityRepository entities, TicketRepository tickets) {
        this.entities = entities;
        this.tickets = tickets;
    }

    @Transactional(readOnly = true)
    public PageView<TicketView> list(
            int page, int size, String sort, String direction, Map<String, String> filters) {
        if (page < 0 || size < 1 || size > 100 || (long) page * size > Integer.MAX_VALUE) {
            throw new BusinessException(400, "Некорректная страница: размер от 1 до 100");
        }
        return new PageView<>(
                tickets.page(page, size, sort, direction, filters).stream().map(ViewMapper::of).toList(),
                tickets.count(filters),
                page,
                size);
    }

    @Transactional(readOnly = true)
    public TicketView get(long id) {
        return ViewMapper.of(entities.require(Ticket.class, id));
    }

    public TicketView save(Long id, TicketRequest request) {
        Ticket ticket = id == null ? new Ticket() : entities.require(Ticket.class, id);
        ticket.setName(request.name());
        ticket.setCoordinates(entities.require(Coordinates.class, request.coordinatesId()));
        ticket.setPerson(
                request.personId() == null
                        ? null
                        : entities.require(Person.class, request.personId()));
        ticket.setEvent(entities.require(Event.class, request.eventId()));
        ticket.setPrice(request.price());
        ticket.setType(request.type());
        ticket.setDiscount(request.discount());
        ticket.setNumber(request.number());
        ticket.setVenue(entities.require(Venue.class, request.venueId()));
        if (id == null) {
            entities.persist(ticket);
        } else {
            entities.flush();
        }
        return ViewMapper.of(ticket);
    }

    public void delete(long id) {
        entities.remove(entities.require(Ticket.class, id));
    }
}
