package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.newspost.CreateNewNewsPostDto;
import com.laboschqpa.server.api.dto.ugc.newspost.EditNewsPostDto;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.NewsPost;
import com.laboschqpa.server.exceptions.ContentNotFoundApiException;
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
        Optional<NewsPost> newsPostOptional = newsPostRepository.findByIdWithEagerAttachments(newsPostId);

        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find NewsPost with Id: " + newsPostId);

        return newsPostOptional.get();
    }

    public NewsPost createNewsPost(CreateNewNewsPostDto createNewNewsPostDto, UserAcc creatorUserAcc) {
        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(createNewNewsPostDto.getAttachments());

        NewsPost newsPost = new NewsPost();
        newsPost.setUGCAsCreatedByUser(creatorUserAcc);
        newsPost.setAttachments(createNewNewsPostDto.getAttachments());

        newsPost.setContent(createNewNewsPostDto.getContent());

        newsPostRepository.save(newsPost);
        logger.info("NewsPost {} created by user {}.", newsPost.getId(), creatorUserAcc.getId());
        return newsPost;
    }

    public void editNewsPost(EditNewsPostDto editNewsPostDto, UserAcc editorUserAcc) {
        Long newsPostId = editNewsPostDto.getId();
        Optional<NewsPost> newsPostOptional = newsPostRepository.findById(newsPostId);
        if (newsPostOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find NewsPost with Id: " + newsPostId);

        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(editNewsPostDto.getAttachments());

        NewsPost newsPost = newsPostOptional.get();
        newsPost.setUGCAsEditedByUser(editorUserAcc);
        newsPost.setAttachments(editNewsPostDto.getAttachments());


        newsPost.setContent(editNewsPostDto.getContent());

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
