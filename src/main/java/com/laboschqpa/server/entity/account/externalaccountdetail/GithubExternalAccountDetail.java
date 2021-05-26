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
@Table(name = "github_external_account_detail",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"github_id"}, name = "github_id__unique")
        }
)
public class GithubExternalAccountDetail extends ExternalAccountDetail implements Serializable {
    static final long serialVersionUID = 42L;

    @Column(name = "github_id", unique = true, nullable = false)
    private String githubId;

    @Override
    public String getDetailString() {
        return githubId;
    }

    @Override
    public void fillFromDetailString(String detailsString) {
        this.setGithubId(detailsString);
    }
}
