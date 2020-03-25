package com.laboschqpa.server.api.dto;

import com.laboschqpa.server.enums.FileAccessType;
import com.laboschqpa.server.util.SelfValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternalResourceDto extends SelfValidator<InternalResourceDto> {
    @Min(1)
    @NotNull
    private Long storedFileId;
    @NotNull
    private FileAccessType fileAccessType;
}
