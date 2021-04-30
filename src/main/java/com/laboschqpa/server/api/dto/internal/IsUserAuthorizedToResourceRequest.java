package com.laboschqpa.server.api.dto.internal;

import com.laboschqpa.server.enums.filehost.FileAccessType;
import com.laboschqpa.server.util.SelfValidator;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IsUserAuthorizedToResourceRequest extends SelfValidator {
    /**
     * Unencoded (NOT Base64 encoded) session ID
     */
    @NotNull
    private String sessionId;
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
