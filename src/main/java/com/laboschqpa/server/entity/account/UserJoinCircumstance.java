package com.laboschqpa.server.entity.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_join_circumstance",
        indexes = {
                @Index(columnList = "user_id", name = "user"),
        }
)
public class UserJoinCircumstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAcc userAcc;

    @Column(name = "join_url", columnDefinition = "text")
    private String joinUrl;

    @Column(name = "created", columnDefinition = "datetime", nullable = false)
    private Instant created;

}