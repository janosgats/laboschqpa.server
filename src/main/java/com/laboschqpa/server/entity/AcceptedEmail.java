package com.laboschqpa.server.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "accepted_email",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email"}, name = "email__unique")
        }
)
public class AcceptedEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "created", columnDefinition = "datetime", nullable = false)
    private Instant created;

    public static AcceptedEmail of(String email) {
        AcceptedEmail created = new AcceptedEmail();
        created.setEmail(email);
        created.setCreated(Instant.now());

        return created;
    }
}
