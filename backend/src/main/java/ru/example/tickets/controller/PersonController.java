package ru.example.tickets.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.example.tickets.dto.request.PersonRequest;
import ru.example.tickets.dto.view.PersonView;
import ru.example.tickets.service.PersonService;

@RestController
@RequestMapping("/api/references/persons")
public class PersonController {
    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @GetMapping
    public List<PersonView> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public PersonView get(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonView create(@RequestBody @Valid PersonRequest request) {
        return service.save(null, request);
    }

    @PutMapping("/{id}")
    public PersonView update(@PathVariable long id, @RequestBody @Valid PersonRequest request) {
        return service.save(id, request);
    }

    @GetMapping("/{id}/links")
    public Map<String, Long> links(@PathVariable long id) {
        return Map.of("count", service.links(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id, @RequestParam(required = false) Long replacementId) {
        service.delete(id, replacementId);
    }
}
