package ru.example.tickets.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.PersonRequest;
import ru.example.tickets.dto.view.PersonView;
import ru.example.tickets.entity.Person;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.LocationRepository;
import ru.example.tickets.repository.PersonRepository;
import ru.example.tickets.repository.TicketRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService {
    private final PersonRepository repository;
    private final TicketRepository links;
    private final LocationRepository locations;

    @Transactional(readOnly = true)
    public List<PersonView> list() {
        return repository.findAll(Sort.by("id")).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public PersonView get(long id) {
        return ViewMapper.of(requirePerson(id));
    }

    public PersonView save(Long id, PersonRequest request) {
        Person entity = id == null ? new Person() : requirePerson(id);
        entity.setEyeColor(request.eyeColor());
        entity.setHairColor(request.hairColor());
        entity.setWeight(request.weight());
        entity.setLocation(locations.findById(request.locationId())
                .orElseThrow(() -> new BusinessException(404, "Локация не найдена")));
        return ViewMapper.of(repository.saveAndFlush(entity));
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return links.countByPerson(requirePerson(id));
    }

    public void delete(long id, Long replacementId) {
        if (replacementId != null && replacementId == id) {
            throw new BusinessException(400, "Для замены выберите другой объект");
        }
        Person source = requireForUpdate(id);
        Person replacement = replacementId == null ? null : requirePerson(replacementId);
        long count = links.countByPerson(source);
        if (count > 0) {
            if (replacement == null) {
                throw new BusinessException(409, "Объект используется. Выберите замену.");
            }
            links.reassignPerson(source, replacement);
        }
        repository.delete(source);
        repository.flush();
    }

    private Person requirePerson(long id) {
        if (id <= 0) throw new BusinessException(404, "Объект не найден: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Объект не найден: " + id));
    }

    private Person requireForUpdate(long id) {
        if (id <= 0) throw new BusinessException(404, "Объект не найден: " + id);
        return repository.findForUpdate(id)
                .orElseThrow(() -> new BusinessException(404, "Объект не найден: " + id));
    }
}
