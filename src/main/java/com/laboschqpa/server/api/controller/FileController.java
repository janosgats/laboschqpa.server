package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.file.AttachmentInfoResponse;
import com.laboschqpa.server.api.dto.file.FileInfoResponse;
import com.laboschqpa.server.api.dto.file.ReadBulkAttachmentInfoRequest;
import com.laboschqpa.server.api.service.TeamService;
import com.laboschqpa.server.api.service.UserService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.exceptions.UnAuthorizedException;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.service.apiclient.filehost.FileHostApiClient;
import com.laboschqpa.server.service.apiclient.filehost.dto.GetIndexedFileInfoResponse;
import com.laboschqpa.server.service.fileaccess.FileAccessAuthorizer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/file")
public class FileController {
    private final FileHostApiClient fileHostApiClient;
    private final TeamService teamService;
    private final UserService userService;
    private final FileAccessAuthorizer fileAccessAuthorizer;

    @PostMapping("/readBulkAttachmentInfo")
    public List<AttachmentInfoResponse> postGetBulkFileInfo(@RequestBody ReadBulkAttachmentInfoRequest request) {
        request.validateSelf();

        return Arrays.stream(fileHostApiClient.getIndexedFileInfo(request.getFileIds()))
                .map(AttachmentInfoResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/info")
    public FileInfoResponse postGetBulkFileInfo(@Param("id") Long id,
                                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final GetIndexedFileInfoResponse basicFileInfo = fileHostApiClient.getIndexedFileInfo(Set.of(id))[0];

        if (!basicFileInfo.isExisting()) {
            throw new ContentNotFoundException("File " + id + " does not exist");
        }

        final UserAcc ownerUser = userService.getById(basicFileInfo.getOwnerUserId());
        final Team ownerTeam = teamService.getById(basicFileInfo.getOwnerTeamId());

        final FileAccessAuthorizer.File file = new FileAccessAuthorizer.File(basicFileInfo);

        final boolean isVisibleForUser = fileAccessAuthorizer.canUserReadFile(authenticationPrincipal.getUserAccEntity(), file);

        return new FileInfoResponse(basicFileInfo, isVisibleForUser, ownerUser, ownerTeam);
    }

    @DeleteMapping("/delete")
    public void deleteDelete(@Param("id") Long id,
                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final GetIndexedFileInfoResponse fileInfo = fileHostApiClient.getIndexedFileInfo(Set.of(id))[0];
        FileAccessAuthorizer.File file = new FileAccessAuthorizer.File(fileInfo);

        if (!fileAccessAuthorizer.canUserDeleteFile(authenticationPrincipal.getUserAccEntity(), file)) {
            throw new UnAuthorizedException("You are not authorized to delete this file");
        }

        fileHostApiClient.deleteFile(id);
    }
}
