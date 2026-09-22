package ru.example.tickets.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.EventRequest;
import ru.example.tickets.dto.view.EventView;
import ru.example.tickets.entity.Event;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.EntityRepository;

@Service
@Transactional
public class EventService {
    private final EntityRepository repository;
    private final ReferenceDeletionService deletion;

    public EventService(EntityRepository repository, ReferenceDeletionService deletion) {
        this.repository = repository;
        this.deletion = deletion;
    }

    @Transactional(readOnly = true)
    public List<EventView> list() {
        return repository.all(Event.class).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public EventView get(long id) {
        return ViewMapper.of(repository.require(Event.class, id));
    }

    public EventView save(Long id, EventRequest request) {
        Event entity = id == null ? new Event() : repository.require(Event.class, id);
        entity.setName(request.name());
        entity.setTicketsCount(request.ticketsCount());
        entity.setEventType(request.eventType());
        if (id == null) {
            repository.persist(entity);
        } else {
            repository.flush();
        }
        return ViewMapper.of(entity);
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return repository.countLinks("Ticket", "event", repository.require(Event.class, id));
    }

    public void delete(long id, Long replacementId) {
        deletion.delete(Event.class, "Ticket", "event", id, replacementId);
    }
}
