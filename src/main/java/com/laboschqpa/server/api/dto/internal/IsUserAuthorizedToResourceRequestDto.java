package com.laboschqpa.server.api.dto.internal;

import com.laboschqpa.server.enums.filehost.FileAccessType;
import com.laboschqpa.server.util.SelfValidator;
import lombok.*;
import org.springframework.http.HttpMethod;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IsUserAuthorizedToResourceRequestDto extends SelfValidator {
    @NotNull
    private HttpMethod httpMethod;
    private String csrfToken;
    @NotNull
    private FileAccessType fileAccessType;
    @Min(1)
    private Long indexedFileId;
    @Min(1)
    private Long indexedFileOwnerUserId;
    @Min(1)
    private Long indexedFileOwnerTeamId;
}
