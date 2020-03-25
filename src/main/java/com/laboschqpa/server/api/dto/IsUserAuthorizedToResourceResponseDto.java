package com.laboschqpa.server.api.dto;

import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IsUserAuthorizedToResourceResponseDto extends SelfValidator<IsUserAuthorizedToResourceResponseDto> {
    @Builder.Default
    private boolean authenticated = true;
    @Builder.Default
    private boolean authorized = false;

    private StoredFileDto storedFileDto;
}

