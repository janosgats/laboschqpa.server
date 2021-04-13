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
        return new GetNewsPostResponse(newsPostService.getNewsPost(newsPostId), true);
    }

    @GetMapping("/listAll")
    public List<GetNewsPostResponse> getListAllNewsPosts() {
        return newsPostService.listAllNewsPosts().stream()
                .map(GetNewsPostResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNewsPost(@RequestBody CreateNewNewsPostRequest createNewNewsPostRequest,
                                                    @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewNewsPostRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        long newId = newsPostService.createNewsPost(createNewNewsPostRequest, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEditNewsPost(@RequestBody EditNewsPostRequest editNewsPostRequest,
                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editNewsPostRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        newsPostService.editNewsPost(editNewsPostRequest, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteNewsPost(@RequestParam(name = "id") Long newsPostId,
                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        newsPostService.deleteNewsPost(newsPostId, authenticationPrincipal.getUserAccEntity());
    }
}
