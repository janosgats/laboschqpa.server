package com.laboschqpa.server.api.dto.ugc.program;

import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentResponse;
import com.laboschqpa.server.entity.usergeneratedcontent.Program;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetProgramJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetProgramWithTeamScoreJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.ProgramDtoAdapter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetProgramResponse extends GetUserGeneratedContentResponse {
    private String title;
    private String headline;
    private String description;
    private Instant startTime;
    private Instant endTime;

    private Integer teamScore;

    public GetProgramResponse() {
        super();
    }

    public GetProgramResponse(Program program) {
        this(program, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetProgramResponse(Program program, boolean includeAttachments) {
        this(new ProgramDtoAdapter(program), includeAttachments);
    }

    public GetProgramResponse(GetProgramJpaDto program) {
        this(program, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetProgramResponse(GetProgramJpaDto program, boolean includeAttachments) {
        super(program, includeAttachments);
        this.title = program.getTitle();
        this.headline = program.getHeadline();
        this.description = program.getDescription();
        this.startTime = program.getStartTimeAsInstant();
        this.endTime = program.getEndTimeAsInstant();

        if (program instanceof GetProgramWithTeamScoreJpaDto) {
            this.teamScore = ((GetProgramWithTeamScoreJpaDto) program).getTeamScore();
        }
    }
}
