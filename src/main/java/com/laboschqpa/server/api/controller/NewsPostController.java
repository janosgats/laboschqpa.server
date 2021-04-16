package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.ugc.newspost.CreateNewNewsPostRequest;
import com.laboschqpa.server.api.dto.ugc.newspost.EditNewsPostRequest;
import com.laboschqpa.server.api.dto.ugc.newspost.GetNewsPostResponse;
import com.laboschqpa.server.api.service.NewsPostService;
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
@RequestMapping(path = "/api/newsPost")
public class NewsPostController {
    private final NewsPostService newsPostService;

    @GetMapping("/newsPost")
    public GetNewsPostResponse getNewsPost(@RequestParam(name = "id") Long newsPostId) {
        return new GetNewsPostResponse(newsPostService.get(newsPostId), true);
    }

    @GetMapping("/listAll")
    public List<GetNewsPostResponse> getListAll() {
        return newsPostService.listAll().stream()
                .map(GetNewsPostResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/listAllWithAttachments")
    public List<GetNewsPostResponse> getListAllWithAttachments() {
        return newsPostService.listAllWithAttachments().stream()
                .map(np -> new GetNewsPostResponse(np, true))
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNew(@RequestBody CreateNewNewsPostRequest createNewNewsPostRequest,
                                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewNewsPostRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        long newId = newsPostService.create(createNewNewsPostRequest, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEdit(@RequestBody EditNewsPostRequest editNewsPostRequest,
                         @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editNewsPostRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        newsPostService.edit(editNewsPostRequest, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteDelete(@RequestParam(name = "id") Long newsPostId,
                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        newsPostService.delete(newsPostId, authenticationPrincipal.getUserAccEntity());
    }
}
