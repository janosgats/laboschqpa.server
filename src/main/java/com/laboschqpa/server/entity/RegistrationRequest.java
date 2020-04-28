package com.laboschqpa.server.entity;

import com.laboschqpa.server.enums.RegistrationRequestPhase;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "registration_request")
public class RegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "_key", nullable = false)
    private String key;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phase", nullable = false)
    private RegistrationRequestPhase phase;
}
