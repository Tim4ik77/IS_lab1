package ru.example.tickets.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.example.tickets.dto.request.LocationRequest;
import ru.example.tickets.dto.view.LocationView;
import ru.example.tickets.service.LocationService;

@RestController
@RequestMapping("/api/references/locations")
public class LocationController {
    private final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping
    public List<LocationView> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public LocationView get(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationView create(@RequestBody @Valid LocationRequest request) {
        return service.save(null, request);
    }

    @PutMapping("/{id}")
    public LocationView update(@PathVariable long id, @RequestBody @Valid LocationRequest request) {
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
