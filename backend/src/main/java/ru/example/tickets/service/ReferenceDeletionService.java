package ru.example.tickets.service;

import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.tickets.exception.BusinessException;
import ru.example.tickets.repository.EntityRepository;

/** Reassigns incoming references and removes the source in the same transaction. */
@Service
@Transactional
public class ReferenceDeletionService {
    private final EntityRepository repository;

    public ReferenceDeletionService(EntityRepository repository) {
        this.repository = repository;
    }

    public <T> void delete(
            Class<T> type, String referencingEntity, String referencingField,
            long id, Long replacementId) {
        if (replacementId != null && replacementId == id) {
            throw new BusinessException(400, "Для замены выберите другой объект");
        }
        // Entity and field names come only from typed services, never from HTTP input.
        // Lock in identifier order to avoid opposite replacement deadlocks.
        T replacement = null;
        if (replacementId != null && replacementId < id) {
            replacement = repository.require(type, replacementId, LockModeType.PESSIMISTIC_WRITE);
        }
        T source = repository.require(type, id, LockModeType.PESSIMISTIC_WRITE);
        if (replacementId != null && replacement == null) {
            replacement = repository.require(type, replacementId, LockModeType.PESSIMISTIC_WRITE);
        }
        long links = repository.countLinks(referencingEntity, referencingField, source);
        if (links > 0 && replacement == null) {
            throw new BusinessException(
                    409, "Объект используется (ссылок: " + links + "). Выберите замену.");
        }
        if (links > 0) {
            repository.reassign(referencingEntity, referencingField, source, replacement);
        }
        repository.remove(source);
    }
}
