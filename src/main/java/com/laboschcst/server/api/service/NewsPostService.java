package com.laboschcst.server.api.service;

import com.laboschcst.server.entity.usergeneratedcontent.NewsPost;
import com.laboschcst.server.exceptions.ContentNotFoundApiException;
import com.laboschcst.server.repo.Repos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsPostService {
    Repos repos;

    public NewsPostService(Repos repos) {
        this.repos = repos;
    }

    public NewsPost getNewsPost(Long newsPostId) {
        Optional<NewsPost> newsPostOptional = repos.newsPostRepository.findById(newsPostId);

        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find NewsPost with newsPostId: " + newsPostId);

        return newsPostOptional.get();
    }

    public List<NewsPost> listAllNewsPosts() {
        return repos.newsPostRepository.findAllByOrderByCreationTimeDesc();
    }
}
