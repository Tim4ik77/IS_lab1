package ru.example.tickets.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ru.example.tickets.entity.Ticket;

public interface SpecialOperationRepository extends Repository<Ticket, Long> {
    @Query(value = "select count_tickets_venue_greater(cast(?1 as integer))", nativeQuery = true)
    long countGreater(int venueId);

    @Query(value = "select * from find_tickets_name_prefix(cast(?1 as text))", nativeQuery = true)
    List<Ticket> prefix(String prefix);

    @Query(value = "select * from find_tickets_venue_less(cast(?1 as integer))", nativeQuery = true)
    List<Ticket> less(int venueId);

    // SELECT returns an ID: @Modifying must not be used here.
    @Query(
            value =
                    "select sell_ticket(cast(?1 as bigint), cast(?2 as bigint), cast(?3 as integer))",
            nativeQuery = true)
    long sell(long ticketId, long personId, int amount);

    @Query(
            value = "select clone_ticket_with_discount(cast(?1 as bigint), cast(?2 as bigint))",
            nativeQuery = true)
    long cloneTicket(long ticketId, long discount);
}
