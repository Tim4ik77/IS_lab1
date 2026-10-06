package ru.example.tickets.repository;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.example.tickets.entity.Ticket;
import ru.example.tickets.enums.TicketType;
import ru.example.tickets.exception.BusinessException;

public final class TicketSpecifications {
    private TicketSpecifications() {}

    private static final Map<String, String> FIELDS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("name", "name"),
            Map.entry("eventName", "event.name"),
            Map.entry("venueName", "venue.name"),
            Map.entry("locationName", "person.location.name"),
            Map.entry("type", "type"));

    public static Sort sort(String field, String direction) {
        if (!FIELDS.containsKey(field) || !("asc".equals(direction) || "desc".equals(direction))) {
            throw new BusinessException(400, "Недопустимое поле или направление сортировки");
        }
        Sort sort = Sort.by(Sort.Direction.fromString(direction), FIELDS.get(field));
        return field.equals("id") ? sort : sort.and(Sort.by("id"));
    }

    public static Specification<Ticket> matching(Map<String, String> filters) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            for (var filter : filters.entrySet()) {
                String field = filter.getKey();
                if (!FIELDS.containsKey(field) || field.equals("id")) {
                    throw new BusinessException(400, "Недопустимое поле фильтрации");
                }
                Path<?> path = switch (field) {
                    case "eventName" -> root.join("event").get("name");
                    case "venueName" -> root.join("venue").get("name");
                    case "locationName" -> root.join("person", JoinType.LEFT)
                            .join("location", JoinType.LEFT).get("name");
                    default -> root.get(field);
                };
                Object value = filter.getValue();
                if (field.equals("type")) {
                    try {
                        value = TicketType.valueOf(filter.getValue());
                    } catch (IllegalArgumentException e) {
                        throw new BusinessException(400, "Неизвестный тип билета");
                    }
                }
                predicates.add(cb.equal(path, value));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
