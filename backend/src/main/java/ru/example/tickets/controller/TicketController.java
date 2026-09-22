package ru.example.tickets.controller;

import ru.example.tickets.dto.request.TicketRequest;
import ru.example.tickets.dto.view.PageView;
import ru.example.tickets.dto.view.TicketView;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import ru.example.tickets.service.TicketService;

import java.util.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public PageView<TicketView> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String venueName,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) String type) {
        var filters = new LinkedHashMap<String, String>();
        if (name != null) filters.put("name", name);
        if (eventName != null) filters.put("eventName", eventName);
        if (venueName != null) filters.put("venueName", venueName);
        if (locationName != null) filters.put("locationName", locationName);
        if (type != null) filters.put("type", type);
        return service.list(page, size, sort, direction, filters);
    }

    @GetMapping("/{id}")
    public TicketView get(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public TicketView create(@RequestBody @Valid TicketRequest r) {
        return service.save(null, r);
    }

    @PutMapping("/{id}")
    public TicketView update(@PathVariable long id, @RequestBody @Valid TicketRequest r) {
        return service.save(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
