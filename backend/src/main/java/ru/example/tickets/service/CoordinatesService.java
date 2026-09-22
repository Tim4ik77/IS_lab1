package ru.example.tickets.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.CoordinatesRequest;
import ru.example.tickets.dto.view.CoordinatesView;
import ru.example.tickets.entity.Coordinates;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.EntityRepository;

@Service
@Transactional
public class CoordinatesService {
    private final EntityRepository repository;
    private final ReferenceDeletionService deletion;

    public CoordinatesService(EntityRepository repository, ReferenceDeletionService deletion) {
        this.repository = repository;
        this.deletion = deletion;
    }

    @Transactional(readOnly = true)
    public List<CoordinatesView> list() {
        return repository.all(Coordinates.class).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public CoordinatesView get(long id) {
        return ViewMapper.of(repository.require(Coordinates.class, id));
    }

    public CoordinatesView save(Long id, CoordinatesRequest request) {
        Coordinates entity = id == null ? new Coordinates() : repository.require(Coordinates.class, id);
        if (!Float.isFinite(request.x())) {
            throw new BusinessException(400, "Координаты должны быть конечными числами");
        }
        entity.setX(request.x());
        entity.setY(request.y());
        if (id == null) {
            repository.persist(entity);
        } else {
            repository.flush();
        }
        return ViewMapper.of(entity);
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return repository.countLinks("Ticket", "coordinates", repository.require(Coordinates.class, id));
    }

    public void delete(long id, Long replacementId) {
        deletion.delete(Coordinates.class, "Ticket", "coordinates", id, replacementId);
    }
}
