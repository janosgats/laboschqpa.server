package com.laboschqpa.server.service.apiclient.filehost.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.laboschqpa.server.enums.filehost.IndexedFileStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetIndexedFileInfoResponse {
    @JsonAlias("isExisting")
    private boolean isExisting;
    private Long indexedFileId;
    private IndexedFileStatus indexedFileStatus;
    private Long ownerUserId;
    private Long ownerTeamId;
    private Instant creationTime;
    private String mimeType;
    private String name;
    private Long size;
}
