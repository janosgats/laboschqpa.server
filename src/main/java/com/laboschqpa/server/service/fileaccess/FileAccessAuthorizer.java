package com.laboschqpa.server.service.fileaccess;

import com.laboschqpa.server.entity.account.UserAcc;
import lombok.Data;

public interface FileAccessAuthorizer {
    @Data
    class File {
        private Long id;
        private Long ownerUserId;
        private Long ownerTeamId;
    }

    boolean canUserReadFile(UserAcc userAcc, File file);
}
