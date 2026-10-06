package ru.example.tickets.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.EventRequest;
import ru.example.tickets.dto.view.EventView;
import ru.example.tickets.entity.Event;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.EventRepository;
import ru.example.tickets.repository.TicketRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final EventRepository repository;
    private final TicketRepository links;

    @Transactional(readOnly = true)
    public List<EventView> list() {
        return repository.findAll(Sort.by("id")).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public EventView get(long id) {
        return ViewMapper.of(requireEvent(id));
    }

    public EventView save(Long id, EventRequest request) {
        Event entity = id == null ? new Event() : requireEvent(id);
        entity.setName(request.name());
        entity.setTicketsCount(request.ticketsCount());
        entity.setEventType(request.eventType());
        return ViewMapper.of(repository.saveAndFlush(entity));
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return links.countByEvent(requireEvent(id));
    }

    public void delete(long id, Long replacementId) {
        if (replacementId != null && replacementId == id) {
            throw new BusinessException(400, "Для замены выберите другой объект");
        }
        Event source = requireForUpdate(id);
        Event replacement = replacementId == null ? null : requireEvent(replacementId);
        long count = links.countByEvent(source);
        if (count > 0) {
            if (replacement == null) {
                throw new BusinessException(409, "Объект используется. Выберите замену.");
            }
            links.reassignEvent(source, replacement);
        }
        repository.delete(source);
        repository.flush();
    }

    private Event requireEvent(long id) {
        if (id <= 0) throw new BusinessException(404, "Объект не найден: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Объект не найден: " + id));
    }

    private Event requireForUpdate(long id) {
        if (id <= 0) throw new BusinessException(404, "Объект не найден: " + id);
        return repository.findForUpdate(id)
                .orElseThrow(() -> new BusinessException(404, "Объект не найден: " + id));
    }
}
