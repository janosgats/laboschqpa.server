package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.emailaddress.UserEmailAddressResponse;
import com.laboschqpa.server.api.service.EmailAddressService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.repo.UserEmailAddressRepository;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/emailAddress")
@Validated
public class EmailAddressController {
    private final UserEmailAddressRepository userEmailAddressRepository;
    private final EmailAddressService emailAddressService;

    @GetMapping("/listOwnAddresses")
    public List<UserEmailAddressResponse> getListOwnAddresses(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final long loggedInUserId = authenticationPrincipal.getUserId();
        return userEmailAddressRepository.findAllByUserAccId(loggedInUserId).stream()
                .map(UserEmailAddressResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/submitNewAddress")
    public void postSubmitEmail(@Email @RequestParam("email") String email,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        emailAddressService.onSubmitNewEmail(authenticationPrincipal.getUserAccEntity(), email);
    }

    @GetMapping("/listAddressesOfUser")
    public List<UserEmailAddressResponse> getListAddressesOfUser(@NotNull @Param("userId") Long userId,
                                                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAdminAuthority();

        return userEmailAddressRepository.findAllByUserAccId(userId).stream()
                .map(UserEmailAddressResponse::new)
                .collect(Collectors.toList());
    }
}
