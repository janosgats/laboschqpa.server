package com.laboschqpa.server.api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.NewsPost;
import com.laboschqpa.server.enums.errorkey.InvalidAttachmentApiError;
import com.laboschqpa.server.exceptions.ContentNotFoundApiException;
import com.laboschqpa.server.exceptions.ugc.InvalidAttachmentException;
import com.laboschqpa.server.repo.usergeneratedcontent.NewsPostRepository;
import com.laboschqpa.server.util.AttachmentHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NewsPostService {
    private static final Logger logger = LoggerFactory.getLogger(NewsPostService.class);

    private final NewsPostRepository newsPostRepository;
    private final AttachmentHelper attachmentHelper;

    public NewsPost getNewsPost(Long newsPostId) {
        Optional<NewsPost> newsPostOptional = newsPostRepository.findById(newsPostId);

        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find NewsPost with Id: " + newsPostId);

        return newsPostOptional.get();
    }

    public void createNewsPost(ObjectNode newsPostContent, UserAcc creatorUserAcc) {
//        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(createNewObjectiveDto.getAttachments());//TODO: Use a dto here!

        NewsPost newsPost = new NewsPost();
        newsPost.setUGCAsCreatedByUser(creatorUserAcc);
//        newsPost.setAttachments(createNewObjectiveDto.getAttachments());//TODO: Use a dto here!

        newsPost.setContent(newsPostContent.get("content").asText());

        newsPostRepository.save(newsPost);
        logger.info("NewsPost {} created by user {}.", newsPost.getId(), creatorUserAcc.getId());
    }

    public void editNewsPost(ObjectNode newsPostContent, UserAcc editorUserAcc) {
//        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(createNewObjectiveDto.getAttachments());//TODO: Use a dto here!

        Long newsPostId = newsPostContent.get("id").asLong();
        Optional<NewsPost> newsPostOptional = newsPostRepository.findById(newsPostId);
        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find NewsPost with Id: " + newsPostId);

        NewsPost newsPost = newsPostOptional.get();
        newsPost.setUGCAsEditedByUser(editorUserAcc);
//        newsPost.setAttachments(createNewObjectiveDto.getAttachments());//TODO: Use a dto here!


        newsPost.setContent(newsPostContent.get("content").asText());

        newsPostRepository.save(newsPost);

        logger.info("NewsPost {} edited by user {}.", newsPost.getId(), editorUserAcc.getId());
    }

    @Transactional
    public void deleteNewsPost(Long newsPostId, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = newsPostRepository.deleteByIdAndGetDeletedRowCount(newsPostId)) != 1) {
            throw new ContentNotFoundApiException("Count of deleted rows is " + deletedRowCount + "!");
        }

        logger.info("NewsPost {} deleted by user {}.", newsPostId, deleterUserAcc.getId());
    }

    public List<NewsPost> listAllNewsPosts() {
        return newsPostRepository.findAllByOrderByCreationTimeDesc();
    }
}
