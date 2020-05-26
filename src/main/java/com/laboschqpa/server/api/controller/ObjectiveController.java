package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ugc.objective.CreateNewObjectiveDto;
import com.laboschqpa.server.api.dto.ugc.objective.EditObjectiveDto;
import com.laboschqpa.server.api.dto.ugc.objective.GetObjectiveDto;
import com.laboschqpa.server.api.service.ObjectiveService;
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
@RequestMapping(path = "/api/objective")
public class ObjectiveController {
    private final ObjectiveService objectiveService;

    @GetMapping("/objective")
    public GetObjectiveDto getObjective(@RequestParam(name = "id") Long objectiveId) {
        return new GetObjectiveDto(objectiveService.getObjective(objectiveId), true);
    }

    @GetMapping("/listAll")
    public List<GetObjectiveDto> getListAllObjectives() {
        return objectiveService.listAllObjectives().stream()
                .map(GetObjectiveDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public void postCreateNewObjective(@RequestBody CreateNewObjectiveDto createNewObjectiveDto,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewObjectiveDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.ObjectiveEditor, Authority.Admin);
        objectiveService.createNewObjective(createNewObjectiveDto, authenticationPrincipal.getUserAccEntity());
    }

    @PostMapping("/edit")
    public void postEditObjective(@RequestBody EditObjectiveDto editObjectiveDto,
                                  @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editObjectiveDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.ObjectiveEditor, Authority.Admin);
        objectiveService.editObjective(editObjectiveDto, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteObjective(@RequestParam(name = "objectiveId") Long objectiveId,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.ObjectiveEditor, Authority.Admin);
        objectiveService.deleteObjective(objectiveId, authenticationPrincipal.getUserAccEntity());
    }
}
