package com.laboschqpa.server.api.dto.file;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.filehost.IndexedFileStatus;
import com.laboschqpa.server.service.apiclient.filehost.dto.GetIndexedFileInfoResponse;
import lombok.Data;

import java.time.Instant;

@Data
public class FileInfoResponse {
    private Long id;
    private String name;
    private IndexedFileStatus status;
    private Long ownerUserId;
    private Long ownerTeamId;
    private Instant creationTime;
    private String mimeType;
    private Long size;

    private Boolean isVisibleForUser;

    private String ownerUserFirstName;
    private String ownerUserLastName;
    private String ownerUserNickName;
    private String ownerTeamName;

    /**
     * The ownerTeam has to be provided based on {@link #ownerTeamId} because the user might have a different team now
     * than they had when they uploaded the file
     */
    public FileInfoResponse(GetIndexedFileInfoResponse dto, boolean isVisibleForUser, UserAcc ownerUser, Team ownerTeam) {
        this.id = dto.getIndexedFileId();
        this.name = dto.getName();
        this.status = dto.getIndexedFileStatus();
        this.ownerUserId = dto.getOwnerUserId();
        this.ownerTeamId = dto.getOwnerTeamId();
        this.creationTime = dto.getCreationTime();
        this.mimeType = dto.getMimeType();
        this.size = dto.getSize();

        this.isVisibleForUser = isVisibleForUser;

        if (ownerUser != null) {
            this.ownerUserFirstName = ownerUser.getFirstName();
            this.ownerUserLastName = ownerUser.getLastName();
            this.ownerUserNickName = ownerUser.getNickName();
        }

        if (ownerTeam != null) {
            this.ownerTeamName = ownerTeam.getName();
        }
    }
}
