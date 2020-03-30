package com.laboschqpa.server.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class IsUserAuthorizedToResourceResponseDto{
    @Builder.Default
    private boolean authorized = false;
    private boolean authenticated;
    private boolean csrfValid;
}

