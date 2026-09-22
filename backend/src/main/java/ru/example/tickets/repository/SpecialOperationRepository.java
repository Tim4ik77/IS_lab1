package ru.example.tickets.repository;

import jakarta.persistence.*;

import org.springframework.stereotype.Repository;

import ru.example.tickets.entity.Ticket;

import java.util.List;

@Repository
public class SpecialOperationRepository {
    @PersistenceContext
    private EntityManager em;

    public long countGreater(int venueId) {
        return ((Number)
                        em.createNativeQuery(
                                        "select count_tickets_venue_greater(cast(?1 as integer))")
                                .setParameter(1, venueId)
                                .getSingleResult())
                .longValue();
    }

    @SuppressWarnings("unchecked")
    public List<Ticket> prefix(String prefix) {
        return em.createNativeQuery(
                        "select * from find_tickets_name_prefix(cast(?1 as text))", Ticket.class)
                .setParameter(1, prefix)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Ticket> less(int venueId) {
        return em.createNativeQuery(
                        "select * from find_tickets_venue_less(cast(?1 as integer))", Ticket.class)
                .setParameter(1, venueId)
                .getResultList();
    }

    public long sell(long ticketId, long personId, int amount) {
        long id =
                ((Number)
                                em.createNativeQuery(
                                                "select sell_ticket(cast(?1 as bigint), cast(?2 as"
                                                        + " bigint), cast(?3 as integer))")
                                        .setParameter(1, ticketId)
                                        .setParameter(2, personId)
                                        .setParameter(3, amount)
                                        .getSingleResult())
                        .longValue();
        em.clear();
        return id;
    }

    public long cloneTicket(long ticketId, long discount) {
        long id =
                ((Number)
                                em.createNativeQuery(
                                                "select clone_ticket_with_discount(cast(?1 as"
                                                        + " bigint), cast(?2 as bigint))")
                                        .setParameter(1, ticketId)
                                        .setParameter(2, discount)
                                        .getSingleResult())
                        .longValue();
        em.clear();
        return id;
    }
}
