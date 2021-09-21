package com.laboschqpa.server.api.dto.qrfight;

import com.laboschqpa.server.repo.dto.QrTagWithSubmissionCountJpaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrTagForUserResponse {
    private Long id;
    private Integer submissionCount;

    public QrTagForUserResponse(QrTagWithSubmissionCountJpaDto jpaDto) {
        this.id = jpaDto.getId();
        this.submissionCount = jpaDto.getSubmissionCount();
    }
}
