package com.laboschqpa.server.api.dto.ugc.program;

import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetProgramWithTeamScoreJpaDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetProgramWithTeamScoreResponse extends GetProgramResponse {

    private Integer teamScore;

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetProgramWithTeamScoreResponse(GetProgramWithTeamScoreJpaDto program, boolean includeAttachments) {
        super(program, includeAttachments);
        this.teamScore = program.getTeamScore();
    }
}
