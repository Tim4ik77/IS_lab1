package ru.example.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.example.tickets.entity.Coordinates;
import ru.example.tickets.entity.Event;
import ru.example.tickets.entity.Person;
import ru.example.tickets.entity.Ticket;
import ru.example.tickets.entity.Venue;

public interface TicketRepository
        extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    long countByCoordinates(Coordinates source);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Ticket e set e.coordinates = ?2 where e.coordinates = ?1")
    int reassignCoordinates(Coordinates source, Coordinates replacement);

    long countByPerson(Person source);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Ticket e set e.person = ?2 where e.person = ?1")
    int reassignPerson(Person source, Person replacement);

    long countByEvent(Event source);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Ticket e set e.event = ?2 where e.event = ?1")
    int reassignEvent(Event source, Event replacement);

    long countByVenue(Venue source);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Ticket e set e.venue = ?2 where e.venue = ?1")
    int reassignVenue(Venue source, Venue replacement);
}
