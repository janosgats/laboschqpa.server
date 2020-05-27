package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ugc.riddle.CreateNewRiddleDto;
import com.laboschqpa.server.api.dto.ugc.riddle.EditRiddleDto;
import com.laboschqpa.server.api.dto.ugc.riddle.GetRiddleDto;
import com.laboschqpa.server.api.service.RiddleEditorService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.service.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/riddleEditor")
public class RiddleEditorController {
    private final RiddleEditorService riddleEditorService;

    @GetMapping("/riddle")
    public GetRiddleDto getObjective(@RequestParam(name = "id") Long riddleId,
                                     @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.RiddleEditor, Authority.Admin);
        return new GetRiddleDto(riddleEditorService.getRiddle(riddleId), true, true, true);
    }

    @GetMapping("/listAll")
    public List<GetRiddleDto> getListAllObjectives(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.RiddleEditor, Authority.Admin);
        return riddleEditorService.listAllRiddles().stream()
                .map((riddle -> new GetRiddleDto(riddle, true, true, false)))
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public Long postCreateNewObjective(@RequestBody CreateNewRiddleDto createNewRiddleDto,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewRiddleDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.RiddleEditor, Authority.Admin);
        return riddleEditorService.createNewRiddle(createNewRiddleDto, authenticationPrincipal.getUserAccEntity()).getId();
    }

    @PostMapping("/edit")
    public void postEditObjective(@RequestBody EditRiddleDto editRiddleDto,
                                  @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editRiddleDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.RiddleEditor, Authority.Admin);
        riddleEditorService.editRiddle(editRiddleDto, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteObjective(@RequestParam(name = "id") Long riddleId,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.RiddleEditor, Authority.Admin);
        riddleEditorService.deleteRiddle(riddleId, authenticationPrincipal.getUserAccEntity());
    }
}
