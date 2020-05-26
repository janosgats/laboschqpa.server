package com.laboschqpa.server.api.dto.ugc;

import com.laboschqpa.server.entity.usergeneratedcontent.UserGeneratedContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetUserGeneratedContentDto {
    private Long creatorUserId;
    private Long editorUserId;
    private Instant creationTime;
    private Instant editTime;
    private Set<Long> attachments;

    public GetUserGeneratedContentDto(UserGeneratedContent userGeneratedContent) {
        this(userGeneratedContent, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be get
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetUserGeneratedContentDto(UserGeneratedContent userGeneratedContent, boolean includeAttachments) {
        this.creatorUserId = userGeneratedContent.getCreatorUser().getId();
        this.editorUserId = userGeneratedContent.getEditorUser().getId();
        this.creationTime = userGeneratedContent.getCreationTime();
        this.editTime = userGeneratedContent.getEditTime();

        if (includeAttachments) {
            this.attachments = userGeneratedContent.getAttachments();
        }
    }
}
