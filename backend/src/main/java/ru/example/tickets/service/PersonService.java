package ru.example.tickets.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.dto.request.PersonRequest;
import ru.example.tickets.dto.view.PersonView;
import ru.example.tickets.entity.Person;
import ru.example.tickets.entity.Location;
import ru.example.tickets.mapper.ViewMapper;
import ru.example.tickets.repository.EntityRepository;

@Service
@Transactional
public class PersonService {
    private final EntityRepository repository;
    private final ReferenceDeletionService deletion;

    public PersonService(EntityRepository repository, ReferenceDeletionService deletion) {
        this.repository = repository;
        this.deletion = deletion;
    }

    @Transactional(readOnly = true)
    public List<PersonView> list() {
        return repository.all(Person.class).stream().map(ViewMapper::of).toList();
    }

    @Transactional(readOnly = true)
    public PersonView get(long id) {
        return ViewMapper.of(repository.require(Person.class, id));
    }

    public PersonView save(Long id, PersonRequest request) {
        Person entity = id == null ? new Person() : repository.require(Person.class, id);
        entity.setEyeColor(request.eyeColor());
        entity.setHairColor(request.hairColor());
        entity.setWeight(request.weight());
        entity.setLocation(repository.require(Location.class, request.locationId()));
        if (id == null) {
            repository.persist(entity);
        } else {
            repository.flush();
        }
        return ViewMapper.of(entity);
    }

    @Transactional(readOnly = true)
    public long links(long id) {
        return repository.countLinks("Ticket", "person", repository.require(Person.class, id));
    }

    public void delete(long id, Long replacementId) {
        deletion.delete(Person.class, "Ticket", "person", id, replacementId);
    }
}
