package ru.example.tickets.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.example.tickets.enums.TicketType;

@Entity
@Table(name = "ticket")
@Getter
@Setter
public class Ticket {
    @Id
    @SequenceGenerator(name = "ticket_ids", sequenceName = "ticket_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_ids")
    @Positive
    private Long id;

    @NotNull
    @Size(min = 1)
    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @NotNull
    @Column(name = "creation_date", nullable = false, updatable = false)
    private java.time.LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Positive
    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    @Min(1)
    @Max(100)
    @Column(nullable = false)
    private long discount;

    @Positive private Long number;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @PrePersist
    void initializeDate() {
        creationDate = java.time.LocalDateTime.now();
    }
}
