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
@Table(name = "google_external_account_detail",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"sub"}, name = "sub__unique")
        }
)
public class GoogleExternalAccountDetail extends ExternalAccountDetail implements Serializable {
    static final long serialVersionUID = 42L;

    @Column(name = "sub", nullable = false)
    private String sub;

    @Override
    public String getDetailString() {
        return sub;
    }

    @Override
    public void fillFromDetailString(String detailsString) {
        this.setSub(detailsString);
    }
}
