package ru.example.tickets.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.example.tickets.entity.Location;
import ru.example.tickets.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Person e where e.id = ?1")
    Optional<Person> findForUpdate(Long id);

    long countByLocation(Location source);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Person e set e.location = ?2 where e.location = ?1")
    int reassignLocation(Location source, Location replacement);
}
