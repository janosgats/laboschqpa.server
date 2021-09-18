package com.laboschqpa.server.entity.event;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@MappedSuperclass
public abstract class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "created", columnDefinition = "datetime")
    private Instant created;
}

