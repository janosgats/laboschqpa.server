package com.laboschqpa.server.api.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsUserAuthorizedToResourceResponseDto{
    @Builder.Default
    private boolean authorized = false;
    private boolean authenticated;
    private boolean csrfValid;

    private Long loggedInUserId;
    private Long loggedInUserTeamId;
}

