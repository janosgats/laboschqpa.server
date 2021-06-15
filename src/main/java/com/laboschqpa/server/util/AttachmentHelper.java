package com.laboschqpa.server.util;

import com.laboschqpa.server.enums.apierrordescriptor.InvalidAttachmentApiError;
import com.laboschqpa.server.enums.filehost.IndexedFileStatus;
import com.laboschqpa.server.exceptions.apierrordescriptor.InvalidAttachmentException;
import com.laboschqpa.server.service.apiclient.filehost.FileHostApiClient;
import com.laboschqpa.server.service.apiclient.filehost.dto.GetIndexedFileInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class AttachmentHelper {
    private static final String MIME_TYPE_IMAGE = "image";
    private final FileHostApiClient fileHostApiClient;

    public void assertAllFilesAvailableAndHaveOwnerUserOf(Set<Long> indexedFileIds, long loggedInUserId) {
        assertAllFilesAvailableAndHaveOwnerUserOf(indexedFileIds, loggedInUserId, false);
    }

    public void assertAllFilesAvailableAndHaveOwnerUserOf(Set<Long> indexedFileIds, long loggedInUserId, boolean enforceImageMimeType) {
        FileCheckResult fileCheckResult = areAllFilesAvailableAndHaveOwnerUserOf(indexedFileIds, loggedInUserId, enforceImageMimeType);
        switch (fileCheckResult) {
            case OK:
                return;
            case FILES_ARE_NOT_AVAILABLE:
                throw new InvalidAttachmentException(InvalidAttachmentApiError.SOME_FILES_ARE_NOT_AVAILABLE,
                        "Some files are not yet available. All the attachments must be available when you submit them.");
            case FILES_ARE_NOT_OWNER_BY_SPECIFIED_USER:
                throw new InvalidAttachmentException(InvalidAttachmentApiError.SOME_FILES_ARE_NOT_OWNED_BY_YOU,
                        "You can only attach files owned by you!");
            case FILE_MIME_TYPES_ARE_NOT_IMAGE:
                throw new InvalidAttachmentException(InvalidAttachmentApiError.FILE_MIME_TYPES_ARE_NOT_IMAGE,
                        "You can only attach images here!");
            default:
                throw new IllegalStateException("Unexpected value: " + fileCheckResult);
        }
    }

    public FileCheckResult areAllFilesAvailableAndHaveOwnerUserOf(Set<Long> indexedFileIds, long ownerUserId, boolean enforceImageMimeType) {
        if (indexedFileIds == null || indexedFileIds.size() == 0)
            return FileCheckResult.OK;

        GetIndexedFileInfoResponse[] indexedFileInfoArray = fileHostApiClient.getIndexedFileInfo(indexedFileIds);
        Set<Long> remainingIdsToCheck = new HashSet<>(indexedFileIds);

        for (GetIndexedFileInfoResponse fileInfo : indexedFileInfoArray) {
            if (fileInfo.isExisting() || fileInfo.getIndexedFileStatus() == IndexedFileStatus.AVAILABLE) {
                remainingIdsToCheck.remove(fileInfo.getIndexedFileId());
            }
            if (ownerUserId != fileInfo.getOwnerUserId()) {
                return FileCheckResult.FILES_ARE_NOT_OWNER_BY_SPECIFIED_USER;
            }
            if (enforceImageMimeType) {
                if (fileInfo.getMimeType() == null || !fileInfo.getMimeType().startsWith(MIME_TYPE_IMAGE + "/")) {
                    return FileCheckResult.FILE_MIME_TYPES_ARE_NOT_IMAGE;
                }
            }
        }
        log.trace("remainingIdsToCheck: {}", () -> remainingIdsToCheck.stream().map(String::valueOf).collect(Collectors.joining(",")));
        if (remainingIdsToCheck.size() == 0) {
            return FileCheckResult.OK;
        }
        return FileCheckResult.FILES_ARE_NOT_AVAILABLE;
    }

    private enum FileCheckResult {
        OK,
        FILES_ARE_NOT_AVAILABLE,
        FILES_ARE_NOT_OWNER_BY_SPECIFIED_USER,
        FILE_MIME_TYPES_ARE_NOT_IMAGE
    }
}
