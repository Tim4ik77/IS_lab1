package ru.example.tickets.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.example.tickets.enums.EventType;

@Entity
@Table(name = "event")
@Getter
@Setter
public class Event {
    @Id
    @SequenceGenerator(name = "event_ids", sequenceName = "event_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_ids")
    @Positive
    private Long id;

    @NotNull
    @Size(min = 1)
    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Positive
    @Column(name = "tickets_count", nullable = false)
    private long ticketsCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;
}
