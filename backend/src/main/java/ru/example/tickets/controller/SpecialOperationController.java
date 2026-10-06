package ru.example.tickets.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.example.tickets.dto.request.CloneRequest;
import ru.example.tickets.dto.request.SellRequest;
import ru.example.tickets.dto.view.TicketView;
import ru.example.tickets.service.SpecialOperationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operations")
public class SpecialOperationController {
    private final SpecialOperationService service;

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
