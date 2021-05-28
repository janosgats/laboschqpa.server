package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.admin.AcceptedEmailResponse;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.AcceptedEmailRepository;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/acceptedEmail")
@Validated
public class AcceptedEmailController {
    private final TransactionTemplate transactionTemplate;
    private final AcceptedEmailRepository acceptedEmailRepository;

    @GetMapping("/listAll")
    public List<AcceptedEmailResponse> getListAll(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.AcceptedEmailEditor);
        return acceptedEmailRepository.findAllByOrderByCreatedDesc().stream()
                .map(AcceptedEmailResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/recalculateAll")
    public void postRecalculateAll(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.AcceptedEmailEditor);
        transactionTemplate.executeWithoutResult(t -> {
            acceptedEmailRepository.recalculateAll();
        });
    }

    @PostMapping("/recalculateByUserId")
    public void postRecalculateByUserId(@RequestBody @NotNull List<@NotNull @Min(0) Long> userIds,
                                        @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.AcceptedEmailEditor);
        transactionTemplate.executeWithoutResult(t -> {
            acceptedEmailRepository.recalculateByUserId(userIds);
        });
    }

    @PostMapping("/recalculateByEmail")
    public void postRecalculateByEmail(@RequestBody @NotNull List<@NotNull String> unfilteredEmails,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.AcceptedEmailEditor);

        final List<String> emails = unfilteredEmails.stream()
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        if (emails.size() > 0) {
            transactionTemplate.executeWithoutResult(t -> {
                acceptedEmailRepository.recalculateByEmail(emails);
            });
        }
    }

    @PostMapping("/addEmails")
    public void postAddEmails(@RequestBody @NotNull List<@NotNull String> unfilteredEmails,
                              @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.AcceptedEmailEditor);

        final List<String> emailsToSave = unfilteredEmails.stream()
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        if (emailsToSave.size() == 0) {
            return;
        }

        Instant now = Instant.now();
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            for (String email : emailsToSave) {
                acceptedEmailRepository.insertOnDuplicateKeyDoNothing(email, now);
            }
            acceptedEmailRepository.flush();

            acceptedEmailRepository.recalculateByEmail(emailsToSave);
        });
    }

    @DeleteMapping("/delete")
    public void deleteDelete(@NotNull @Param("id") Long id,
                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.AcceptedEmailEditor);

        String originalEmail = acceptedEmailRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Accepted e-mail address not found by id: " + id))
                .getEmail();

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            acceptedEmailRepository.deleteById(id);
            acceptedEmailRepository.flush();

            acceptedEmailRepository.recalculateByEmail(originalEmail);
        });
    }
}
