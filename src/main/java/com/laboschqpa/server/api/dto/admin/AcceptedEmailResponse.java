package com.laboschqpa.server.api.dto.admin;

import com.laboschqpa.server.entity.AcceptedEmail;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class AcceptedEmailResponse {
    private Long id;
    private String email;
    private Instant created;

    public AcceptedEmailResponse(AcceptedEmail entity) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.created = entity.getCreated();
    }
}
