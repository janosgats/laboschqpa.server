package com.laboschqpa.server.entity.qrtagfight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qr_tag")
public class QrTag {
    public QrTag(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Immutable field!
     */
    @Column(name = "secret", nullable = false)
    private String secret;
}
