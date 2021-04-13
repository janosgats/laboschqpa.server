package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.newspost.CreateNewNewsPostRequest;
import com.laboschqpa.server.api.dto.ugc.newspost.EditNewsPostRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.NewsPost;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.usergeneratedcontent.NewsPostRepository;
import com.laboschqpa.server.util.AttachmentHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NewsPostService {
    private static final Logger logger = LoggerFactory.getLogger(NewsPostService.class);

    private final NewsPostRepository newsPostRepository;
    private final AttachmentHelper attachmentHelper;

    public NewsPost getNewsPost(Long newsPostId) {
        Optional<NewsPost> newsPostOptional = newsPostRepository.findByIdWithEagerAttachments(newsPostId);

        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find NewsPost with Id: " + newsPostId);

        return newsPostOptional.get();
    }

    public NewsPost createNewsPost(CreateNewNewsPostRequest createNewNewsPostRequest, UserAcc creatorUserAcc) {
        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(createNewNewsPostRequest.getAttachments());

        NewsPost newsPost = new NewsPost();
        newsPost.setUGCAsCreatedByUser(creatorUserAcc);
        newsPost.setAttachments(createNewNewsPostRequest.getAttachments());

        newsPost.setContent(createNewNewsPostRequest.getContent());

        newsPostRepository.save(newsPost);
        logger.info("NewsPost {} created by user {}.", newsPost.getId(), creatorUserAcc.getId());
        return newsPost;
    }

    public void editNewsPost(EditNewsPostRequest editNewsPostRequest, UserAcc editorUserAcc) {
        Long newsPostId = editNewsPostRequest.getId();
        Optional<NewsPost> newsPostOptional = newsPostRepository.findById(newsPostId);
        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find NewsPost with Id: " + newsPostId);

        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(editNewsPostRequest.getAttachments());

        NewsPost newsPost = newsPostOptional.get();
        newsPost.setUGCAsEditedByUser(editorUserAcc);
        newsPost.setAttachments(editNewsPostRequest.getAttachments());


        newsPost.setContent(editNewsPostRequest.getContent());

        newsPostRepository.save(newsPost);

        logger.info("NewsPost {} edited by user {}.", newsPost.getId(), editorUserAcc.getId());
    }

    @Transactional
    public void deleteNewsPost(Long newsPostId, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = newsPostRepository.deleteByIdAndGetDeletedRowCount(newsPostId)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }

        logger.info("NewsPost {} deleted by user {}.", newsPostId, deleterUserAcc.getId());
    }

    public List<NewsPost> listAllNewsPosts() {
        return newsPostRepository.findAllByOrderByCreationTimeDesc();
    }
}
