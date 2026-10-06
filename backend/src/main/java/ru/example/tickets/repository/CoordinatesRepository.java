package ru.example.tickets.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.example.tickets.entity.Coordinates;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Coordinates e where e.id = ?1")
    Optional<Coordinates> findForUpdate(Long id);
}
