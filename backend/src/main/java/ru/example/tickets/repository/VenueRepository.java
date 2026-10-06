package ru.example.tickets.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.example.tickets.entity.Venue;

public interface VenueRepository extends JpaRepository<Venue, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Venue e where e.id = ?1")
    Optional<Venue> findForUpdate(Integer id);
}
