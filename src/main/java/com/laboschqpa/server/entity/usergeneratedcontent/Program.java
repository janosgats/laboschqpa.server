package com.laboschqpa.server.entity.usergeneratedcontent;

import com.laboschqpa.server.enums.ugc.UserGeneratedContentTypeValues;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "program",
        indexes = {
                @Index(columnList = "start_time", name = "start_time")
        }
)
@DiscriminatorValue(value = UserGeneratedContentTypeValues.PROGRAM)
public class Program extends UserGeneratedContent {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "headline")
    private String headline;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;
}
