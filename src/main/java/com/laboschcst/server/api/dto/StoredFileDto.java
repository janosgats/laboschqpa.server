package com.laboschcst.server.api.dto;

import com.laboschcst.server.entity.StoredFile;
import com.laboschcst.server.enums.StoredFileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoredFileDto {
    private Long id;
    private StoredFileStatus status;
    private String path;
    private Long originalUploaderUserId;
    private Long currentUploaderUserId;
    private Long size;

    public StoredFileDto(StoredFile storedFile) {
        this.id = storedFile.getId();
        this.status = storedFile.getStatus();
        this.path = storedFile.getPath();
        this.originalUploaderUserId = storedFile.getOriginalUploaderUser().getId();
        this.currentUploaderUserId = storedFile.getCurrentUploaderUser().getId();
        this.size = storedFile.getSize();
    }
}
