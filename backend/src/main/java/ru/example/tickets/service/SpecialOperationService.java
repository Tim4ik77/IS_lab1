package ru.example.tickets.service;

import ru.example.tickets.dto.view.TicketView;
import ru.example.tickets.mapper.ViewMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.example.tickets.repository.SpecialOperationRepository;

import java.util.List;

@Service
@Transactional
public class SpecialOperationService {
    private final SpecialOperationRepository operations;
    private final TicketService tickets;

    public SpecialOperationService(SpecialOperationRepository operations, TicketService tickets) {
        this.operations = operations;
        this.tickets = tickets;
    }

    @Transactional(readOnly = true)
    public long count(int venue) {
        return operations.countGreater(venue);
    }

    @Transactional(readOnly = true)
    public List<TicketView> prefix(String prefix) {
        return operations.prefix(prefix).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public List<TicketView> less(int venue) {
        return operations.less(venue).stream().map(ViewMapper::of).toList();
    }

    public TicketView sell(long ticket, long person, int amount) {
        return tickets.get(operations.sell(ticket, person, amount));
    }

    public TicketView cloneTicket(long ticket, long discount) {
        return tickets.get(operations.cloneTicket(ticket, discount));
    }
}
