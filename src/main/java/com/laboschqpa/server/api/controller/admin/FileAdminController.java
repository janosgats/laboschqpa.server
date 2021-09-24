package com.laboschqpa.server.api.controller.admin;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.service.apiclient.filehost.FileHostApiClient;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/admin/file")
public class FileAdminController {
    private final FileHostApiClient fileHostApiClient;

    @GetMapping("/listSucceededImageVariantIdsOfFile")
    public List<Long> getListSucceededImageVariantIdsOfFile(@Param("originalFileId") Long originalFileId,
                                                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.FileSupervisor);

        return Arrays.asList(fileHostApiClient.listSucceededImageVariantIdsOfFile(originalFileId));
    }

    @PostMapping("/markImageVariantFileAsCorrupt")
    public void postMarkImageVariantFileAsCorrupt(@Param("variantFileId") Long variantFileId,
                                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.FileSupervisor);

        fileHostApiClient.markImageVariantFileAsCorruptUrl(variantFileId);
    }
}
