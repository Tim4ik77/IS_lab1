package ru.example.tickets.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.VenueRequest;
import ru.example.tickets.dto.view.VenueView;
import ru.example.tickets.entity.Venue;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.EntityRepository;

@Service
@Transactional
public class VenueService {
    private final EntityRepository repository;
    private final ReferenceDeletionService deletion;

    public VenueService(EntityRepository repository, ReferenceDeletionService deletion) {
        this.repository = repository;
        this.deletion = deletion;
    }

    @Transactional(readOnly = true)
    public List<VenueView> list() {
        return repository.all(Venue.class).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public VenueView get(long id) {
        return ViewMapper.of(repository.require(Venue.class, id));
    }

    public VenueView save(Long id, VenueRequest request) {
        Venue entity = id == null ? new Venue() : repository.require(Venue.class, id);
        entity.setName(request.name());
        entity.setCapacity(request.capacity());
        entity.setType(request.type());
        if (id == null) {
            repository.persist(entity);
        } else {
            repository.flush();
        }
        return ViewMapper.of(entity);
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return repository.countLinks("Ticket", "venue", repository.require(Venue.class, id));
    }

    public void delete(long id, Long replacementId) {
        deletion.delete(Venue.class, "Ticket", "venue", id, replacementId);
    }
}
