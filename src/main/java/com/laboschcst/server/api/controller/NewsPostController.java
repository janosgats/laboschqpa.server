package com.laboschcst.server.api.controller;

import com.laboschcst.server.api.service.NewsPostService;
import com.laboschcst.server.entity.usergeneratedcontent.NewsPost;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/newspost")
public class NewsPostController {
    private final NewsPostService newsPostService;

    public NewsPostController(NewsPostService newsPostService) {
        this.newsPostService = newsPostService;
    }

    @GetMapping("/newspost")
    public NewsPost getProfileDetails(@RequestParam(name = "newsPostId") Long newsPostId) {
        return newsPostService.getNewsPost(newsPostId);
    }

    @GetMapping("/listall")
    public List<NewsPost> getListAllNewsPosts() {
        return newsPostService.listAllNewsPosts();
    }
}
