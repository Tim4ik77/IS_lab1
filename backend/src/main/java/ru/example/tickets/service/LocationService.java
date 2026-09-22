package ru.example.tickets.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.LocationRequest;
import ru.example.tickets.dto.view.LocationView;
import ru.example.tickets.entity.Location;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.EntityRepository;

@Service
@Transactional
public class LocationService {
    private final EntityRepository repository;
    private final ReferenceDeletionService deletion;

    public LocationService(EntityRepository repository, ReferenceDeletionService deletion) {
        this.repository = repository;
        this.deletion = deletion;
    }

    @Transactional(readOnly = true)
    public List<LocationView> list() {
        return repository.all(Location.class).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public LocationView get(long id) {
        return ViewMapper.of(repository.require(Location.class, id));
    }

    public LocationView save(Long id, LocationRequest request) {
        Location entity = id == null ? new Location() : repository.require(Location.class, id);
        if (!Float.isFinite(request.x()) || !Float.isFinite(request.z())) {
            throw new BusinessException(400, "Координаты должны быть конечными числами");
        }
        entity.setX(request.x());
        entity.setY(request.y());
        entity.setZ(request.z());
        entity.setName(request.name());
        if (id == null) {
            repository.persist(entity);
        } else {
            repository.flush();
        }
        return ViewMapper.of(entity);
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return repository.countLinks("Person", "location", repository.require(Location.class, id));
    }

    public void delete(long id, Long replacementId) {
        deletion.delete(Location.class, "Person", "location", id, replacementId);
    }
}
