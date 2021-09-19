package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.TeamScoreResponse;
import com.laboschqpa.server.api.dto.ugc.program.CreateNewProgramRequest;
import com.laboschqpa.server.api.dto.ugc.program.EditProgramRequest;
import com.laboschqpa.server.api.dto.ugc.program.GetProgramResponse;
import com.laboschqpa.server.api.dto.ugc.program.GetProgramWithTeamScoreResponse;
import com.laboschqpa.server.api.service.ProgramService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/program")
public class ProgramController {
    private final ProgramService programService;

    @GetMapping("/program")
    public GetProgramResponse getProgram(@RequestParam(name = "id") Long id) {
        return new GetProgramResponse(programService.getWithAttachments(id), true);
    }

    @ApiOperation("Returns the score of a team earned on a program")
    @GetMapping("/teamScore")
    public TeamScoreResponse getTeamScore(@RequestParam(name = "programId") Long programId, @RequestParam(name = "teamId") Long teamId) {
        return new TeamScoreResponse(programService.getTeamScore(programId, teamId));
    }

    @GetMapping("/listAll")
    public List<GetProgramResponse> getListAll() {
        return programService.listAll().stream()
                .map(GetProgramResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/listAllWithTeamScore")
    public List<GetProgramResponse> getListAllWithTeamScore(@RequestParam(name = "teamId") Long teamId) {
        return programService.listAllWithTeamScore(teamId).stream()
                .map((program) -> new GetProgramWithTeamScoreResponse(program, false))
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNew(@RequestBody CreateNewProgramRequest request,
                                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        request.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.ProgramEditor);
        long newId = programService.create(request, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEdit(@RequestBody EditProgramRequest request,
                         @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        request.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.ProgramEditor);
        programService.edit(request, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteDelete(@RequestParam(name = "id") Long id,
                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.ProgramEditor);
        programService.delete(id, authenticationPrincipal.getUserAccEntity());
    }
}
