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
                    "name", "ticket.name",
                    "eventName", "ticket.event.name",
                    "venueName", "ticket.venue.name",
                    "locationName", "location.name",
                    "type", "ticket.type");

    private Object parseFilterValue(String fieldName, String value) {
        if (!fieldName.equals("type")) return value;
        try {
            return TicketType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "Неизвестный тип билета");
        }
    }

    private String joins() {
        return " from Ticket ticket left join ticket.person person left join person.location location";
    }

    private String where(Map<String, String> filters) {
        return filters.keySet().stream()
                .map(fieldName -> FIELDS.get(fieldName) + " = :" + fieldName)
                .collect(
                        java.util.stream.Collectors.joining(
                                " and ", filters.isEmpty() ? "" : " where ", ""));
    }

    public List<Ticket> page(
            int page, int size, String sort, String direction, Map<String, String> filters) {
        String column = sort.equals("id") ? "ticket.id" : FIELDS.get(sort);
        if (column == null || !(direction.equals("asc") || direction.equals("desc")))
            throw new BusinessException(400, "Недопустимое поле или направление сортировки");
        var query =
                em.createQuery(
                        "select ticket"
                                + joins()
                                + where(filters)
                                + " order by "
                                + column
                                + " "
                                + direction
                                + (sort.equals("id") ? "" : ", ticket.id asc"),
                        Ticket.class);
        filters.forEach(
                (fieldName, filterValue) ->
                        query.setParameter(fieldName, parseFilterValue(fieldName, filterValue)));
        return query.setFirstResult(Math.multiplyExact(page, size))
                .setMaxResults(size)
                .getResultList();
    }

    public long count(Map<String, String> filters) {
        var query = em.createQuery("select count(ticket)" + joins() + where(filters), Long.class);
        filters.forEach(
                (fieldName, filterValue) ->
                        query.setParameter(fieldName, parseFilterValue(fieldName, filterValue)));
        return query.getSingleResult();
    }
}
