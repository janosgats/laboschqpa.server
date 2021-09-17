package com.laboschqpa.server.entity.event;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.enums.event.EventTargetTypeValues;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "team_event_registration",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"team_id","event_id"}, name = "team_event__unique")
        }
)
@DiscriminatorValue(value = EventTargetTypeValues.PERSONAL)
public class TeamEventRegistration extends EventRegistration {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}
