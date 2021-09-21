package com.laboschqpa.server.entity.qrfight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qr_tag",
        indexes = {
                @Index(columnList = "area_id", name = "area")
        })
public class QrTag {
    public QrTag(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private QrFightArea area;

    /**
     * Immutable field!
     */
    @Column(name = "secret", nullable = false)
    private String secret;
}
