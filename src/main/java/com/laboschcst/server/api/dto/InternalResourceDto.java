package com.laboschcst.server.api.dto;

import com.laboschcst.server.enums.FileAccessType;
import com.laboschcst.server.util.SelfValidator;
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
