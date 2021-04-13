package com.laboschqpa.server.api.dto.ugc.newspost;

import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentDto;
import com.laboschqpa.server.entity.usergeneratedcontent.NewsPost;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetNewsPostResponse extends GetUserGeneratedContentDto {
    private String content;

    public GetNewsPostResponse() {
        super();
    }

    public GetNewsPostResponse(NewsPost newsPost) {
        this(newsPost, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetNewsPostResponse(NewsPost newsPost, boolean includeAttachments) {
        super(newsPost, includeAttachments);
        this.content = newsPost.getContent();
    }
}
