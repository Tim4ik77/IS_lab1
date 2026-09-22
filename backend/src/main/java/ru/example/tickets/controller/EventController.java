package ru.example.tickets.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.example.tickets.dto.request.EventRequest;
import ru.example.tickets.dto.view.EventView;
import ru.example.tickets.service.EventService;

@RestController
@RequestMapping("/api/references/events")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventView> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public EventView get(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventView create(@RequestBody @Valid EventRequest request) {
        return service.save(null, request);
    }

    @PutMapping("/{id}")
    public EventView update(@PathVariable long id, @RequestBody @Valid EventRequest request) {
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
