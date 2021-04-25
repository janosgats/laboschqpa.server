package com.laboschqpa.server.api.dto.file;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReadBulkAttachmentInfoRequest extends SelfValidator {
    @NotNull
    private Set<@NotNull @Min(1) Long> fileIds;
}
