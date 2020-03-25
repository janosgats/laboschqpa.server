package com.laboschqpa.server.entity;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.StoredFileStatus;
import com.laboschqpa.server.enums.attributeconverter.StoredFileStatusAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stored_file")
public class StoredFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Convert(converter = StoredFileStatusAttributeConverter.class)
    @Column(name = "status", nullable = false)
    private StoredFileStatus status;

    @Column(name = "path", nullable = false)
    private String path;//Generated as: "<YYYY>/<MM>/<DD>/file<id>.sf"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_uploader_user_id", nullable = false)
    private UserAcc originalUploaderUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_uploader_user_id", nullable = false)
    private UserAcc currentUploaderUser;

    @JoinColumn(name = "size", nullable = false)
    private Long size;//Size in Bytes

}
