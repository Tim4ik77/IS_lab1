package ru.example.tickets.repository;

import jakarta.persistence.*;

import org.springframework.stereotype.Repository;

import ru.example.tickets.entity.Ticket;
import ru.example.tickets.enums.TicketType;
import ru.example.tickets.exception.BusinessException;

import java.util.*;

@Repository
public class TicketRepository {
    @PersistenceContext
    private EntityManager em;
    private static final Map<String, String> FIELDS =
            Map.of(
                    "name", "t.name",
                    "eventName", "t.event.name",
                    "venueName", "t.venue.name",
                    "locationName", "loc.name",
                    "type", "t.type");

    private Object filterValue(String key, String value) {
        if (!key.equals("type")) return value;
        try {
            return TicketType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "Неизвестный тип билета");
        }
    }

    private String joins() {
        return " from Ticket t left join t.person p left join p.location loc";
    }

    private String where(Map<String, String> filters) {
        return filters.keySet().stream()
                .map(k -> FIELDS.get(k) + " = :" + k)
                .collect(
                        java.util.stream.Collectors.joining(
                                " and ", filters.isEmpty() ? "" : " where ", ""));
    }

    public List<Ticket> page(
            int page, int size, String sort, String direction, Map<String, String> filters) {
        String column = sort.equals("id") ? "t.id" : FIELDS.get(sort);
        if (column == null || !(direction.equals("asc") || direction.equals("desc"))) {
            throw new BusinessException(400, "Недопустимое поле или направление сортировки");
        }
        var query = em.createQuery(
                        "select t"
                                + joins()
                                + where(filters)
                                + " order by "
                                + column
                                + " "
                                + direction
                                + (sort.equals("id") ? "" : ", t.id asc"),
                        Ticket.class);
        filters.forEach((k, v) -> query.setParameter(k, filterValue(k, v)));
        return query.setFirstResult(Math.multiplyExact(page, size))
                .setMaxResults(size)
                .getResultList();
    }

    public long count(Map<String, String> filters) {
        var query = em.createQuery("select count(t)" + joins() + where(filters), Long.class);
        filters.forEach((k, v) -> query.setParameter(k, filterValue(k, v)));
        return query.getSingleResult();
    }
}
