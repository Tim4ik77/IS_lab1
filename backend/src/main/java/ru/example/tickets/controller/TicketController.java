package ru.example.tickets.controller;

import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.example.tickets.dto.request.TicketRequest;
import ru.example.tickets.dto.view.PageView;
import ru.example.tickets.dto.view.TicketView;
import ru.example.tickets.service.TicketService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService service;

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
    public TicketView create(@RequestBody @Valid TicketRequest request) {
        return service.save(null, request);
    }

    @PutMapping("/{id}")
    public TicketView update(@PathVariable long id, @RequestBody @Valid TicketRequest request) {
        return service.save(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
