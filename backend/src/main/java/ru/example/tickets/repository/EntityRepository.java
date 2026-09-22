package ru.example.tickets.repository;

import jakarta.persistence.*;

import org.springframework.stereotype.Repository;

import ru.example.tickets.entity.*;
import ru.example.tickets.exception.BusinessException;

import java.util.List;

@Repository
public class EntityRepository {
    @PersistenceContext
    private EntityManager em;

    public <T> T require(Class<T> type, long id) {
        return require(type, id, null);
    }

    public <T> T require(Class<T> type, long id, LockModeType lock) {
        if (id <= 0 || (type == Venue.class && id > Integer.MAX_VALUE)) {
            throw new BusinessException(404, "Объект не найден: " + id);
        }
        Object key;
        if (type == Venue.class) {
            key = Integer.valueOf((int) id);
        } else {
            key = Long.valueOf(id);
        }
        T result = lock == null ? em.find(type, key) : em.find(type, key, lock);
        if (result == null) {
            throw new BusinessException(404, "Объект не найден: " + id);
        }
        return result;
    }

    public <T> List<T> all(Class<T> type) {
        return em.createQuery("select e from " + type.getSimpleName() + " e order by e.id", type)
                .getResultList();
    }

    public void persist(Object object) {
        em.persist(object);
        em.flush();
    }

    public void flush() {
        em.flush();
    }

    public void remove(Object object) {
        em.remove(object);
        em.flush();
    }

    public long countLinks(String entity, String field, Object target) {
        return em.createQuery(
                        "select count(e) from " + entity + " e where e." + field + " = :target",
                        Long.class)
                .setParameter("target", target)
                .getSingleResult();
    }

    public void reassign(String entity, String field, Object source, Object replacement) {
        em.createQuery(
                        "update "
                                + entity
                                + " e set e."
                                + field
                                + " = :replacement where e."
                                + field
                                + " = :source")
                .setParameter("replacement", replacement)
                .setParameter("source", source)
                .executeUpdate();
    }
}
