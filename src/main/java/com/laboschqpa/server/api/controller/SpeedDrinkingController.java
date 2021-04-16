package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.ugc.speeddrinking.CreateNewSpeedDrinkingRequest;
import com.laboschqpa.server.api.dto.ugc.speeddrinking.EditSpeedDrinkingRequest;
import com.laboschqpa.server.api.dto.ugc.speeddrinking.GetSpeedDrinkingResponse;
import com.laboschqpa.server.api.service.SpeedDrinkingService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/speedDrinking")
public class SpeedDrinkingController {
    private final SpeedDrinkingService speedDrinkingService;

    @GetMapping("/speedDrinking")
    public GetSpeedDrinkingResponse getNewsPost(@RequestParam(name = "id") Long speedDrinkingId) {
        return new GetSpeedDrinkingResponse(speedDrinkingService.getSpeedDrinking(speedDrinkingId));
    }

    @GetMapping("/listAll")
    public List<GetSpeedDrinkingResponse> getListAllNewsPosts() {
        return speedDrinkingService.listAllSpeedDrinkings().stream()
                .map(GetSpeedDrinkingResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNewsPost(@RequestBody CreateNewSpeedDrinkingRequest createNewSpeedDrinkingRequest,
                                                    @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewSpeedDrinkingRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.SpeedDrinkingEditor, Authority.Admin);
        long newId = speedDrinkingService.createSpeedDrinking(createNewSpeedDrinkingRequest, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEditNewsPost(@RequestBody EditSpeedDrinkingRequest editSpeedDrinkingRequest,
                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editSpeedDrinkingRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.SpeedDrinkingEditor, Authority.Admin);
        speedDrinkingService.editSpeedDrinking(editSpeedDrinkingRequest, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteNewsPost(@RequestParam(name = "id") Long speedDrinkingId,
                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.SpeedDrinkingEditor, Authority.Admin);
        speedDrinkingService.deleteSpeedDrinking(speedDrinkingId, authenticationPrincipal.getUserAccEntity());
    }
}
