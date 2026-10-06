package ru.example.tickets.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.CoordinatesRequest;
import ru.example.tickets.dto.view.CoordinatesView;
import ru.example.tickets.entity.Coordinates;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.CoordinatesRepository;
import ru.example.tickets.repository.TicketRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CoordinatesService {
    private final CoordinatesRepository repository;
    private final TicketRepository links;

    @Transactional(readOnly = true)
    public List<CoordinatesView> list() {
        return repository.findAll(Sort.by("id")).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public CoordinatesView get(long id) {
        return ViewMapper.of(requireCoordinates(id));
    }

    public CoordinatesView save(Long id, CoordinatesRequest request) {
        Coordinates entity = id == null ? new Coordinates() : requireCoordinates(id);
        entity.setX(request.x());
        entity.setY(request.y());
        return ViewMapper.of(repository.saveAndFlush(entity));
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return links.countByCoordinates(requireCoordinates(id));
    }

    public void delete(long id, Long replacementId) {
        if (replacementId != null && replacementId == id) {
            throw new BusinessException(400, "Для замены выберите другой объект");
        }
        Coordinates source = requireForUpdate(id);
        Coordinates replacement = replacementId == null ? null : requireCoordinates(replacementId);
        long count = links.countByCoordinates(source);
        if (count > 0) {
            if (replacement == null) {
                throw new BusinessException(409, "Объект используется. Выберите замену.");
            }
            links.reassignCoordinates(source, replacement);
        }
        repository.delete(source);
        repository.flush();
    }

    private Coordinates requireCoordinates(long id) {
        if (id <= 0) throw new BusinessException(404, "Объект не найден: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Объект не найден: " + id));
    }

    private Coordinates requireForUpdate(long id) {
        if (id <= 0) throw new BusinessException(404, "Объект не найден: " + id);
        return repository.findForUpdate(id)
                .orElseThrow(() -> new BusinessException(404, "Объект не найден: " + id));
    }
}
