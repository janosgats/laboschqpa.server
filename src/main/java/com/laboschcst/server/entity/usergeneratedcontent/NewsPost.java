package com.laboschcst.server.entity.usergeneratedcontent;

import javax.persistence.*;

@Entity
@Table(name = "news_post")
@Inheritance(strategy = InheritanceType.JOINED)
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
