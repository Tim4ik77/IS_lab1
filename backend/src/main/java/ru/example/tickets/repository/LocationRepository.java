package ru.example.tickets.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.example.tickets.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Location e where e.id = ?1")
    Optional<Location> findForUpdate(Long id);
}
