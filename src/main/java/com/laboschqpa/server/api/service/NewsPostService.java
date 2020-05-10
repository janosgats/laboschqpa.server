package com.laboschqpa.server.api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.NewsPost;
import com.laboschqpa.server.exceptions.ContentNotFoundApiException;
import com.laboschqpa.server.repo.NewsPostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NewsPostService {
    private static final Logger logger = LoggerFactory.getLogger(NewsPostService.class);

    private final NewsPostRepository newsPostRepository;

    public NewsPost getNewsPost(Long newsPostId) {
        Optional<NewsPost> newsPostOptional = newsPostRepository.findById(newsPostId);

        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find NewsPost with Id: " + newsPostId);

        return newsPostOptional.get();
    }

    public void createNewsPost(ObjectNode newsPostContent, UserAcc creatorUserAcc) {
        NewsPost newsPost = new NewsPost();
        newsPost.setContent(newsPostContent.get("content").asText());
        newsPost.setCreatorUser(creatorUserAcc);
        newsPost.setEditorUser(creatorUserAcc);
        newsPost.setCreationTime(Instant.now());
        newsPost.setEditTime(Instant.now());

        newsPostRepository.save(newsPost);
        logger.info("NewsPost {} created by user {}.", newsPost.getId(), creatorUserAcc.getId());
    }

    public void editNewsPost(ObjectNode newsPostContent, UserAcc editorUserAcc) {
        Long newsPostId = newsPostContent.get("id").asLong();
        Optional<NewsPost> newsPostOptional = newsPostRepository.findById(newsPostId);
        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find NewsPost with Id: " + newsPostId);

        NewsPost newsPost = newsPostOptional.get();
        newsPost.setContent(newsPostContent.get("content").asText());
        newsPost.setEditorUser(editorUserAcc);
        newsPost.setEditTime(Instant.now());

        newsPostRepository.save(newsPost);

        logger.info("NewsPost {} edited by user {}.", newsPost.getId(), editorUserAcc.getId());
    }

    public void deleteNewsPost(Long newsPostId, UserAcc deleterUserAcc) {
        if (!newsPostRepository.existsById(newsPostId))
            throw new ContentNotFoundApiException("Cannot find NewsPost with Id: " + newsPostId);

        newsPostRepository.deleteById(newsPostId);

        logger.info("NewsPost {} deleted by user {}.", newsPostId, deleterUserAcc.getId());
    }

    public List<NewsPost> listAllNewsPosts() {
        return newsPostRepository.findAllByOrderByCreationTimeDesc();
    }
}
