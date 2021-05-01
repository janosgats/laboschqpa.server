package com.laboschqpa.server.api.dto.file;

import com.laboschqpa.server.service.apiclient.filehost.dto.GetIndexedFileInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AttachmentInfoResponse {
    private Long fileId;
    private String fileName;

    public AttachmentInfoResponse(GetIndexedFileInfoResponse dto) {
        this.fileId = dto.getIndexedFileId();
        this.fileName = dto.getName();
    }
}
