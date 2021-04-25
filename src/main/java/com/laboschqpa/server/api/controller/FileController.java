package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.file.AttachmentInfoResponse;
import com.laboschqpa.server.api.dto.file.ReadBulkAttachmentInfoRequest;
import com.laboschqpa.server.service.apiclient.filehost.FileHostApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/file")
public class FileController {
    private final FileHostApiClient fileHostApiClient;

    @PostMapping("/readBulkAttachmentInfo")
    public List<AttachmentInfoResponse> postGetBulkFileInfo(@RequestBody ReadBulkAttachmentInfoRequest request) {
        request.validateSelf();

        return Arrays.stream(fileHostApiClient.getIndexedFileInfo(request.getFileIds()))
                .map(AttachmentInfoResponse::new)
                .collect(Collectors.toList());
    }
}
