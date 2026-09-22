package ru.example.tickets.controller;

import ru.example.tickets.dto.request.CloneRequest;
import ru.example.tickets.dto.request.SellRequest;
import ru.example.tickets.dto.view.TicketView;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import ru.example.tickets.service.SpecialOperationService;

import java.util.*;

@RestController
@RequestMapping("/api/operations")
public class SpecialOperationController {
    private final SpecialOperationService service;

    public SpecialOperationController(SpecialOperationService service) {
        this.service = service;
    }

    @GetMapping("/count-venue-greater")
    public Map<String, Long> count(@RequestParam int venueId) {
        return Map.of("count", service.count(venueId));
    }

    @GetMapping("/name-prefix")
    public List<TicketView> prefix(@RequestParam String prefix) {
        return service.prefix(prefix);
    }

    @GetMapping("/venue-less")
    public List<TicketView> less(@RequestParam int venueId) {
        return service.less(venueId);
    }

    @PostMapping("/sell")
    public TicketView sell(@RequestBody @Valid SellRequest request) {
        return service.sell(request.ticketId(), request.personId(), request.amount());
    }

    @PostMapping("/clone")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public TicketView cloneTicket(@RequestBody @Valid CloneRequest request) {
        return service.cloneTicket(request.ticketId(), request.discount());
    }
}
