package com.laboschqpa.server.entity.event;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.event.EventTargetTypeValues;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "personal_event_registration",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id","event_id"}, name = "user_event__unique")
        }
)
@DiscriminatorValue(value = EventTargetTypeValues.PERSONAL)
public class PersonalEventRegistration extends EventRegistration {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAcc userAcc;
}
