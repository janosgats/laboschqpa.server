package com.laboschqpa.server.util;

import com.laboschqpa.server.enums.apierrordescriptor.InvalidAttachmentApiError;
import com.laboschqpa.server.enums.filehost.IndexedFileStatus;
import com.laboschqpa.server.exceptions.apierrordescriptor.InvalidAttachmentException;
import com.laboschqpa.server.service.apiclient.filehost.FileHostApiClient;
import com.laboschqpa.server.service.apiclient.filehost.GetIndexedFileInfoResultDto;
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
    private final FileHostApiClient fileHostApiClient;

    public void assertAllFilesExistAndAvailableOnFileHost(Set<Long> indexedFileIds) {
        if (!areAllFilesExistingAndAvailableOnFileHost(indexedFileIds)) {
            throw new InvalidAttachmentException(InvalidAttachmentApiError.SOME_FILES_ARE_NOT_AVAILABLE,
                    "Some of these files are not available: "
                            + indexedFileIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
    }

    public boolean areAllFilesExistingAndAvailableOnFileHost(Set<Long> indexedFileIds) {
        if (indexedFileIds == null)
            return true;

        GetIndexedFileInfoResultDto[] indexedFileInfoArray = fileHostApiClient.getIndexedFileInfo(indexedFileIds);
        Set<Long> remainingIdsToCheck = new HashSet<>(indexedFileIds);

        for (GetIndexedFileInfoResultDto fileInfo : indexedFileInfoArray) {
            if (fileInfo.isExisting() || fileInfo.getIndexedFileStatus() == IndexedFileStatus.AVAILABLE) {
                remainingIdsToCheck.remove(fileInfo.getIndexedFileId());
            }
        }
        log.trace("remainingIdsToCheck: {}", () -> remainingIdsToCheck.stream().map(String::valueOf).collect(Collectors.joining(",")));
        return remainingIdsToCheck.size() == 0;
    }
}
