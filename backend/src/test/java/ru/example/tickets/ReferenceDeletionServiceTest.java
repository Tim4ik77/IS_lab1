package ru.example.tickets;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.example.tickets.entity.Venue;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.repository.EntityRepository;
import ru.example.tickets.service.ReferenceDeletionService;

class ReferenceDeletionServiceTest {
    private EntityRepository repository;
    private ReferenceDeletionService service;
    private Venue source;
    private Venue replacement;

    @BeforeEach
    void setup() {
        repository = mock(EntityRepository.class);
        service = new ReferenceDeletionService(repository);
        source = new Venue();
        replacement = new Venue();
        when(repository.require(Venue.class, 2, LockModeType.PESSIMISTIC_WRITE)).thenReturn(source);
        when(repository.require(Venue.class, 1, LockModeType.PESSIMISTIC_WRITE)).thenReturn(replacement);
    }

    @Test
    void locksInIdOrderAndReassignsBeforeRemoving() {
        when(repository.countLinks("Ticket", "venue", source)).thenReturn(3L);
        service.delete(Venue.class, "Ticket", "venue", 2, 1L);
        var order = inOrder(repository);
        order.verify(repository).require(Venue.class, 1, LockModeType.PESSIMISTIC_WRITE);
        order.verify(repository).require(Venue.class, 2, LockModeType.PESSIMISTIC_WRITE);
        order.verify(repository).countLinks("Ticket", "venue", source);
        order.verify(repository).reassign("Ticket", "venue", source, replacement);
        order.verify(repository).remove(source);
    }

    @Test
    void linkedObjectCannotBeRemovedWithoutReplacement() {
        when(repository.countLinks("Ticket", "venue", source)).thenReturn(3L);
        assertThatThrownBy(() -> service.delete(Venue.class, "Ticket", "venue", 2, null))
                .isInstanceOfSatisfying(BusinessException.class, error -> assertThat(error.getStatus()).isEqualTo(409));
        verify(repository, never()).remove(any());
        verify(repository, never()).reassign(anyString(), anyString(), any(), any());
    }

    @Test
    void unlinkedObjectCanBeRemovedWithoutReplacement() {
        service.delete(Venue.class, "Ticket", "venue", 2, null);
        verify(repository).remove(source);
        verify(repository, never()).reassign(anyString(), anyString(), any(), any());
    }

    @Test
    void cannotReplaceAnObjectWithItself() {
        assertThatThrownBy(() -> service.delete(Venue.class, "Ticket", "venue", 2, 2L))
                .isInstanceOfSatisfying(BusinessException.class, error -> assertThat(error.getStatus()).isEqualTo(400));
        verifyNoInteractions(repository);
    }
}
