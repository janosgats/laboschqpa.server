package com.laboschqpa.server.api.dto.ugc.riddle;

import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentDto;
import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetRiddleDto extends GetUserGeneratedContentDto {
    private String title;
    private String hint;
    private String solution;

    public GetRiddleDto() {
        super();
    }

    public GetRiddleDto(Riddle riddle, boolean includeHint, boolean includeSolution) {
        this(riddle, includeHint, includeSolution, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetRiddleDto(Riddle riddle, boolean includeHint, boolean includeSolution, boolean includeAttachments) {
        super(riddle, includeAttachments);
        this.title = riddle.getTitle();

        if (includeHint) {
            this.hint = riddle.getHint();
        }
        if (includeSolution) {
            this.solution = riddle.getSolution();
        }
    }
}
