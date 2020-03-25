package com.laboschqpa.server.entity.usergeneratedcontent;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.api.serialization.NewsPostSerializer;

import javax.persistence.*;

@Entity
@Table(name = "news_post")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonSerialize(using = NewsPostSerializer.class)
public class NewsPost extends UserGeneratedContent {
    @Column(name = "content")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
