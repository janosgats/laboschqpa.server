package com.labosch.csillagtura.server.entity.externalaccount;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(indexes = {@Index(columnList = "github_id")})
public class GithubExternalAccountDetail extends ExternalAccountDetail implements Serializable {
    static final long serialVersionUID = 42L;

    @Column(name = "github_id", unique = true, nullable = false)
    private String githubId;

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }
}
