package com.laboschqpa.server.entity.usergeneratedcontent;

import javax.persistence.*;

@Entity
@Table(name = "news_post")
@Inheritance(strategy = InheritanceType.JOINED)
public class NewsPost extends UserGeneratedContent {
    @Column(name = "content")
    private String content;//Possibly Markdown

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
