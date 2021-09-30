package com.laboschqpa.server.api.dto.qrfight;

import com.laboschqpa.server.repo.dto.qrFightArea.QrFightAreaAndTeamSubmissionCountJpaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrFightStatResponse {
    private Long areaId;
    private Long teamId;
    private String teamName;
    private Integer submissionCount;

    public QrFightStatResponse(QrFightAreaAndTeamSubmissionCountJpaDto jpaDto) {
        this.areaId = jpaDto.getAreaId();
        this.teamId = jpaDto.getTeamId();
        this.teamName = jpaDto.getTeamName();
        this.submissionCount = jpaDto.getSubmissionCount();
    }
}
