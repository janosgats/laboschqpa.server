package com.laboschqpa.server.service.fileaccess;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.service.apiclient.filehost.dto.GetIndexedFileInfoResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface FileAccessAuthorizer {
    @Data
    @NoArgsConstructor
    class File {
        private Long id;
        private Long ownerUserId;
        private Long ownerTeamId;

        public File(GetIndexedFileInfoResponse dto) {
            this.id = dto.getIndexedFileId();
            this.ownerUserId = dto.getOwnerUserId();
            this.ownerTeamId = dto.getOwnerTeamId();
        }
    }

    boolean canUserDeleteFile(UserAcc userAcc, File file);

    boolean canUserReadFile(UserAcc userAcc, File file);
}
