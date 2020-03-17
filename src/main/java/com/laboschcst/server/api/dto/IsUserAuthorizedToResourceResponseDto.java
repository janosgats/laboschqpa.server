package com.laboschcst.server.api.dto;

import com.laboschcst.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IsUserAuthorizedToResourceResponseDto extends SelfValidator<IsUserAuthorizedToResourceResponseDto> {
    private boolean authorized;

    private StoredFileDto storedFileDto;
}

