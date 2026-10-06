package ru.example.tickets.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.LocationRequest;
import ru.example.tickets.dto.view.LocationView;
import ru.example.tickets.entity.Location;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.LocationRepository;
import ru.example.tickets.repository.PersonRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    private final LocationRepository repository;
    private final PersonRepository links;

    @Transactional(readOnly = true)
    public List<LocationView> list() {
        return repository.findAll(Sort.by("id")).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public LocationView get(long id) {
        return ViewMapper.of(requireLocation(id));
    }

    public LocationView save(Long id, LocationRequest request) {
        Location entity = id == null ? new Location() : requireLocation(id);
        entity.setX(request.x());
        entity.setY(request.y());
        entity.setZ(request.z());
        entity.setName(request.name());
        return ViewMapper.of(repository.saveAndFlush(entity));
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return links.countByLocation(requireLocation(id));
    }

    public void delete(long id, Long replacementId) {
        if (replacementId != null && replacementId == id) {
            throw new BusinessException(400, "Для замены выберите другой объект");
        }
        Location source = requireForUpdate(id);
        Location replacement = replacementId == null ? null : requireLocation(replacementId);
        long count = links.countByLocation(source);
        if (count > 0) {
            if (replacement == null) {
                throw new BusinessException(409, "Объект используется. Выберите замену.");
            }
            links.reassignLocation(source, replacement);
        }
        repository.delete(source);
        repository.flush();
    }

    private Location requireLocation(long id) {
        if (id <= 0) throw new BusinessException(404, "Объект не найден: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Объект не найден: " + id));
    }

    private Location requireForUpdate(long id) {
        if (id <= 0) throw new BusinessException(404, "Объект не найден: " + id);
        return repository.findForUpdate(id)
                .orElseThrow(() -> new BusinessException(404, "Объект не найден: " + id));
    }
}
