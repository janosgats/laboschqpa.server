package com.laboschqpa.server.api.dto.internal;

import lombok.*;

@ToString
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

