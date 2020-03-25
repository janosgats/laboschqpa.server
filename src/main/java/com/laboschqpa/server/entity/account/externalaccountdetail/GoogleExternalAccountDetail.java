package com.laboschqpa.server.entity.account.externalaccountdetail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(indexes = {@Index(columnList = "sub")})
public class GoogleExternalAccountDetail extends ExternalAccountDetail implements Serializable {
    static final long serialVersionUID = 42L;

    @Column(name = "sub", unique = true, nullable = false)
    private String sub;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }
}
