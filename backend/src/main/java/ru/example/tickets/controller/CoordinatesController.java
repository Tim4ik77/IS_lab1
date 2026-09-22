package ru.example.tickets.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.example.tickets.dto.request.CoordinatesRequest;
import ru.example.tickets.dto.view.CoordinatesView;
import ru.example.tickets.service.CoordinatesService;

@RestController
@RequestMapping("/api/references/coordinates")
public class CoordinatesController {
    private final CoordinatesService service;

    public CoordinatesController(CoordinatesService service) {
        this.service = service;
    }

    @GetMapping
    public List<CoordinatesView> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public CoordinatesView get(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoordinatesView create(@RequestBody @Valid CoordinatesRequest request) {
        return service.save(null, request);
    }

    @PutMapping("/{id}")
    public CoordinatesView update(@PathVariable long id, @RequestBody @Valid CoordinatesRequest request) {
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
