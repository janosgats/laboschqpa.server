package com.laboschqpa.server.entity;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.EmailVerificationPhase;
import com.laboschqpa.server.enums.converter.attributeconverter.EmailVerificationPhaseAttributeConverter;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "email_verification_request")
public class EmailVerificationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAcc userAcc;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "verification_key", nullable = false)
    private String verificationKey;

    @Column(name = "phase", nullable = false)
    @Convert(converter = EmailVerificationPhaseAttributeConverter.class)
    private EmailVerificationPhase phase;
}
