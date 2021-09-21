package com.laboschqpa.server.api.dto.qrfight;

import com.laboschqpa.server.entity.qrfight.QrFightArea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrFightAreaResponse {
    private Long id;
    private String name;

    public QrFightAreaResponse(QrFightArea entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
