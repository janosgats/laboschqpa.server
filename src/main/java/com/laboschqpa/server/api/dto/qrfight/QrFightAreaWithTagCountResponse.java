package com.laboschqpa.server.api.dto.qrfight;

import com.laboschqpa.server.repo.dto.qrFightArea.QrFightAreaWithTagCountJpaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrFightAreaWithTagCountResponse extends QrFightAreaResponse {
    private Integer tagCount;

    public QrFightAreaWithTagCountResponse(QrFightAreaWithTagCountJpaDto jpaDto) {
        super(jpaDto);
        this.tagCount = jpaDto.getTagCount();
    }
}
