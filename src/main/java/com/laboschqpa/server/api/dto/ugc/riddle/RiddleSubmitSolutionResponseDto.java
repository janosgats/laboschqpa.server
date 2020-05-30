package com.laboschqpa.server.api.dto.ugc.riddle;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RiddleSubmitSolutionResponseDto {
    private Boolean isGivenSolutionCorrect;
    private Boolean isCurrentlySolved;
}
