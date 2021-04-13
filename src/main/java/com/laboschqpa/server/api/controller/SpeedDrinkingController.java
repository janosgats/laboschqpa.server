package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.ugc.speeddrinking.CreateNewSpeedDrinkingDto;
import com.laboschqpa.server.api.dto.ugc.speeddrinking.EditSpeedDrinkingDto;
import com.laboschqpa.server.api.dto.ugc.speeddrinking.GetSpeedDrinkingDto;
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
    public GetSpeedDrinkingDto getNewsPost(@RequestParam(name = "id") Long speedDrinkingId) {
        return new GetSpeedDrinkingDto(speedDrinkingService.getSpeedDrinking(speedDrinkingId), true);
    }

    @GetMapping("/listAll")
    public List<GetSpeedDrinkingDto> getListAllNewsPosts() {
        return speedDrinkingService.listAllSpeedDrinkings().stream()
                .map(GetSpeedDrinkingDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNewsPost(@RequestBody CreateNewSpeedDrinkingDto createNewSpeedDrinkingDto,
                                                    @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewSpeedDrinkingDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.SpeedDrinkingEditor, Authority.Admin);
        long newId = speedDrinkingService.createSpeedDrinking(createNewSpeedDrinkingDto, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEditNewsPost(@RequestBody EditSpeedDrinkingDto editSpeedDrinkingDto,
                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editSpeedDrinkingDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.SpeedDrinkingEditor, Authority.Admin);
        speedDrinkingService.editSpeedDrinking(editSpeedDrinkingDto, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteNewsPost(@RequestParam(name = "id") Long speedDrinkingId,
                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.SpeedDrinkingEditor, Authority.Admin);
        speedDrinkingService.deleteSpeedDrinking(speedDrinkingId, authenticationPrincipal.getUserAccEntity());
    }
}
