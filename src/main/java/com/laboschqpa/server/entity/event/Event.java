package com.laboschqpa.server.entity.event;

import com.laboschqpa.server.enums.converter.attributeconverter.EventTargetAttributeConverter;
import com.laboschqpa.server.enums.event.EventTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"}, name = "name__unique")
        }
)
public class Event {
    public Event(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Immutable field! (Because personal and team registrations are two different entities.)
     */
    @Convert(converter = EventTargetAttributeConverter.class)
    @Column(name = "target", nullable = false)
    private EventTarget target;

    /**
     * Max number of attendees. If null, there is no limit.
     */
    @Column(name = "registration_limit")
    private Integer registrationLimit;

    /**
     * If null, there is no deadline.
     */
    @Column(name = "registration_deadline")
    private Instant registrationDeadline;
}
