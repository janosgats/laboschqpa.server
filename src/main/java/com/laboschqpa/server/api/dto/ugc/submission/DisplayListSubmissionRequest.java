package com.laboschqpa.server.api.dto.ugc.submission;

import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DisplayListSubmissionRequest extends SelfValidator {
    private Long objectiveId;
    private Long teamId;
}
