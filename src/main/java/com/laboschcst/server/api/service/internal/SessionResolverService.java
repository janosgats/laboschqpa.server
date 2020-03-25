package com.laboschcst.server.api.service.internal;

import com.laboschcst.server.api.dto.InternalResourceDto;
import com.laboschcst.server.api.dto.IsUserAuthorizedToResourceResponseDto;
import com.laboschcst.server.api.dto.StoredFileDto;
import com.laboschcst.server.config.auth.user.CustomOauth2User;
import com.laboschcst.server.entity.StoredFile;
import com.laboschcst.server.repo.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SessionResolverService {
    private final StoredFileRepository storedFileRepository;

    public IsUserAuthorizedToResourceResponseDto getIsAuthorizedToResource(InternalResourceDto internalResourceDto, CustomOauth2User authenticationPrincipal) {
        internalResourceDto.validateSelf();

        Optional<StoredFile> storedFileOptional = storedFileRepository.findById(internalResourceDto.getStoredFileId());
        if (storedFileOptional.isPresent()) {
            StoredFile storedFile = storedFileOptional.get();

            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .authorized(storedFile.getCurrentUploaderUser().getId().equals(authenticationPrincipal.getUserId()))//TODO: Do proper triage of authorization here
                    .storedFileDto(new StoredFileDto(storedFile))
                    .build();
        } else {
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .authorized(false)
                    .storedFileDto(null)
                    .build();
        }

    }
}
