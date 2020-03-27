package com.laboschqpa.server.api.dto;

import com.laboschqpa.server.enums.FileAccessType;
import com.laboschqpa.server.util.SelfValidator;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IndexedFileServingRequestDto extends SelfValidator<IndexedFileServingRequestDto> {
    @Min(1)
    private Long indexedFileId;
    @NotNull
    private FileAccessType fileAccessType;
}
