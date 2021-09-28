package com.laboschqpa.server.entity.account.externalaccountdetail;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "authsch_external_account_detail",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"internal_id"}, name = "internal_id__unique")
        }
)
public class AuthSchExternalAccountDetail extends ExternalAccountDetail implements Serializable {
    static final long serialVersionUID = 42L;

    @Column(name = "internal_id", nullable = false)
    private String internalId;

    @Override
    public String getDetailString() {
        return internalId;
    }

    @Override
    public void fillFromDetailString(String detailsString) {
        this.setInternalId(detailsString);
    }
}
