package com.laboschqpa.server.entity.ratelimit;

import com.laboschqpa.server.enums.TeamRateControlTopic;
import com.laboschqpa.server.enums.converter.attributeconverter.TeamRateControlTopicAttributeConverter;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "team_rate_control_event",
        indexes = {
                @Index(columnList = "topic, team_id, time", name = "topic__team__time")
        }
)
public class TeamRateControlEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Convert(converter = TeamRateControlTopicAttributeConverter.class)
    @Column(name = "topic", nullable = false)
    private TeamRateControlTopic topic;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "time", columnDefinition = "datetime", nullable = false)
    private Instant time;
}
