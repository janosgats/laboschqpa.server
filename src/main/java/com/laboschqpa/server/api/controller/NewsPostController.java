package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ugc.newspost.CreateNewNewsPostDto;
import com.laboschqpa.server.api.dto.ugc.newspost.EditNewsPostDto;
import com.laboschqpa.server.api.dto.ugc.newspost.GetNewsPostDto;
import com.laboschqpa.server.api.service.NewsPostService;
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
@RequestMapping(path = "/api/newsPost")
public class NewsPostController {
    private final NewsPostService newsPostService;

    @GetMapping("/newsPost")
    public GetNewsPostDto getNewsPost(@RequestParam(name = "newsPostId") Long newsPostId) {
        return new GetNewsPostDto(newsPostService.getNewsPost(newsPostId), true);
    }

    @GetMapping("/listAll")
    public List<GetNewsPostDto> getListAllNewsPosts() {
        return newsPostService.listAllNewsPosts().stream()
                .map(GetNewsPostDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public Long postCreateNewsPost(@RequestBody CreateNewNewsPostDto createNewNewsPostDto,
                                   @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewNewsPostDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        return newsPostService.createNewsPost(createNewNewsPostDto, authenticationPrincipal.getUserAccEntity()).getId();
    }

    @PostMapping("/edit")
    public void postEditNewsPost(@RequestBody EditNewsPostDto editNewsPostDto,
                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editNewsPostDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        newsPostService.editNewsPost(editNewsPostDto, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteNewsPost(@RequestParam(name = "id") Long newsPostId,
                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.NewsPostEditor, Authority.Admin);
        newsPostService.deleteNewsPost(newsPostId, authenticationPrincipal.getUserAccEntity());
    }
}
