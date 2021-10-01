package com.laboschqpa.server.api.dto.ugc.riddleeditor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentResponse;
import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import com.laboschqpa.server.enums.RiddleCategory;
import com.laboschqpa.server.enums.converter.jackson.RiddleCategoryToValueJacksonConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetRiddleResponse extends GetUserGeneratedContentResponse {
    private String title;
    @JsonSerialize(converter = RiddleCategoryToValueJacksonConverter.class)
    private RiddleCategory category;
    private String hint;
    private String solution;

    public GetRiddleResponse() {
        super();
    }

    public GetRiddleResponse(Riddle entity, boolean includeHint, boolean includeSolution) {
        this(entity, includeHint, includeSolution, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetRiddleResponse(Riddle entity, boolean includeHint, boolean includeSolution, boolean includeAttachments) {
        super(entity, includeAttachments);
        this.title = entity.getTitle();
        this.category = entity.getCategory();

        if (includeHint) {
            this.hint = entity.getHint();
        }
        if (includeSolution) {
            this.solution = entity.getSolution();
        }
    }
}
