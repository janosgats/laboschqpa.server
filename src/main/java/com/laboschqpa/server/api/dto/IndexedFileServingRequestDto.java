package com.laboschqpa.server.api.dto;

import com.laboschqpa.server.enums.FileAccessType;
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
public class IndexedFileServingRequestDto extends SelfValidator<IndexedFileServingRequestDto> {
    @NotNull
    private HttpMethod httpMethod;
    private String csrfToken;
    @Min(1)
    @NotNull
    private Long indexedFileId;
    @NotNull
    private FileAccessType fileAccessType;
}
