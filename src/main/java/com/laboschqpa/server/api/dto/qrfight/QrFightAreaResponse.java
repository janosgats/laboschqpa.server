package com.laboschqpa.server.api.dto.qrfight;

import com.laboschqpa.server.entity.qrfight.QrFightArea;
import com.laboschqpa.server.repo.dto.qrFightArea.QrFightAreaDtoAdapter;
import com.laboschqpa.server.repo.dto.qrFightArea.QrFightAreaJpaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrFightAreaResponse {
    private Long id;
    private String name;
    private String description;

    public QrFightAreaResponse(QrFightArea entity) {
        this(new QrFightAreaDtoAdapter(entity));
    }

    public QrFightAreaResponse(QrFightAreaJpaDto jpaDto) {
        this.id = jpaDto.getId();
        this.name = jpaDto.getName();
        this.description = jpaDto.getDescription();
    }
}
