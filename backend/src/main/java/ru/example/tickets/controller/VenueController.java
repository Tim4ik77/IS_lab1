package ru.example.tickets.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.example.tickets.dto.request.VenueRequest;
import ru.example.tickets.dto.view.VenueView;
import ru.example.tickets.service.VenueService;

@RestController
@RequestMapping("/api/references/venues")
public class VenueController {
    private final VenueService service;

    public VenueController(VenueService service) {
        this.service = service;
    }

    @GetMapping
    public List<VenueView> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public VenueView get(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueView create(@RequestBody @Valid VenueRequest request) {
        return service.save(null, request);
    }

    @PutMapping("/{id}")
    public VenueView update(@PathVariable long id, @RequestBody @Valid VenueRequest request) {
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
