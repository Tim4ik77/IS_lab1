package ru.example.tickets.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.view.TicketView;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.SpecialOperationRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialOperationService {
    @PersistenceContext private EntityManager entityManager;
    private final SpecialOperationRepository operations;
    private final TicketService tickets;

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
        entityManager.flush();
        long id = operations.sell(ticket, person, amount);
        entityManager.clear(); // The SQL function updated rows outside the persistence context.
        return tickets.get(id);
    }

    public TicketView cloneTicket(long ticket, long discount) {
        entityManager.flush();
        long id = operations.cloneTicket(ticket, discount);
        entityManager.clear();
        return tickets.get(id);
    }
}
