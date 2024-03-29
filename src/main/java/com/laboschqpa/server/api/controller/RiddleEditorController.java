package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.ugc.riddleeditor.CreateNewRiddleRequest;
import com.laboschqpa.server.api.dto.ugc.riddleeditor.EditRiddleRequest;
import com.laboschqpa.server.api.dto.ugc.riddleeditor.GetRiddleResponse;
import com.laboschqpa.server.api.dto.ugc.riddleeditor.RiddleTeamProgressResponse;
import com.laboschqpa.server.api.service.RiddleEditorService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.RiddleCategory;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
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
    public GetRiddleResponse getRiddle(@RequestParam(name = "id") Long riddleId,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.RiddleEditor);
        return new GetRiddleResponse(riddleEditorService.getRiddle(riddleId), true, true, true);
    }

    @GetMapping("/listAll")
    public List<GetRiddleResponse> getListAllRiddles(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.RiddleEditor);
        return riddleEditorService.listAllRiddles().stream()
                .map((riddle -> new GetRiddleResponse(riddle, true, true, false)))
                .collect(Collectors.toList());
    }

    @GetMapping("/listAllInCategory")
    public List<GetRiddleResponse> getListAllRiddlesInCategory(@RequestParam("category") RiddleCategory category,
                                                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.RiddleEditor);
        return riddleEditorService.listAllRiddlesInCategory(category).stream()
                .map((riddle -> new GetRiddleResponse(riddle, true, true, false)))
                .collect(Collectors.toList());
    }

    @GetMapping("/listProgressOfTeams")
    public List<RiddleTeamProgressResponse> getListProgressOfTeams(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.RiddleEditor);

        return riddleEditorService.listProgressOfTeams().stream()
                .map(RiddleTeamProgressResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNewRiddle(@RequestBody CreateNewRiddleRequest createNewRiddleRequest,
                                                     @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewRiddleRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.RiddleEditor);
        long newId = riddleEditorService.createNewRiddle(createNewRiddleRequest, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEditRiddle(@RequestBody EditRiddleRequest editRiddleRequest,
                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editRiddleRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.RiddleEditor);
        riddleEditorService.editRiddle(editRiddleRequest, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteRiddle(@RequestParam(name = "id") Long riddleId,
                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.RiddleEditor);
        riddleEditorService.deleteRiddle(riddleId, authenticationPrincipal.getUserAccEntity());
    }
}
