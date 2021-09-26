package com.laboschqpa.server.entity.usergeneratedcontent;

import com.laboschqpa.server.enums.ugc.UserGeneratedContentTypeValues;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "news_post")
@DiscriminatorValue(value = UserGeneratedContentTypeValues.NEWS_POST)
public class NewsPost extends UserGeneratedContent {
    @Column(name = "title", columnDefinition = "text", nullable = false)
    private String title;
    @Column(name = "content", columnDefinition = "text")
    private String content;
}
