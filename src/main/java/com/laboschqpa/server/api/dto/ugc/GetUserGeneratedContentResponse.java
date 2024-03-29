package com.laboschqpa.server.api.dto.ugc;

import com.laboschqpa.server.entity.usergeneratedcontent.UserGeneratedContent;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetUserGeneratedContentJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.UserGeneratedContentDtoAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetUserGeneratedContentResponse {
    private Long id;
    private Long creatorUserId;
    private Long editorUserId;
    private Instant creationTime;
    private Instant editTime;
    private Set<Long> attachments;

    public GetUserGeneratedContentResponse(UserGeneratedContent userGeneratedContent) {
        this(userGeneratedContent, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be get
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetUserGeneratedContentResponse(UserGeneratedContent userGeneratedContent, boolean includeAttachments) {
        this(new UserGeneratedContentDtoAdapter(userGeneratedContent), includeAttachments);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be get
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetUserGeneratedContentResponse(GetUserGeneratedContentJpaDto userGeneratedContent, boolean includeAttachments) {
        this.id = userGeneratedContent.getId();
        this.creatorUserId = userGeneratedContent.getCreatorUserId();
        this.editorUserId = userGeneratedContent.getEditorUserId();
        this.creationTime = userGeneratedContent.getCreationTimeAsInstant();
        this.editTime = userGeneratedContent.getEditTimeAsInstant();

        if (includeAttachments) {
            this.attachments = userGeneratedContent.getAttachments();
        }
    }
}
