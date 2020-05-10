package com.laboschqpa.server.entity.usergeneratedcontent;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "news_post")
@Inheritance(strategy = InheritanceType.JOINED)
public class NewsPost extends UserGeneratedContent {
    @Column(name = "content")
    private String content;//Possibly Markdown
}
