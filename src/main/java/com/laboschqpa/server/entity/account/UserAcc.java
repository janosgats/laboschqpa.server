package com.laboschqpa.server.entity.account;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.config.helper.EnumBasedAuthority;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.entity.account.externalaccountdetail.GithubExternalAccountDetail;
import com.laboschqpa.server.entity.account.externalaccountdetail.GoogleExternalAccountDetail;
import com.laboschqpa.server.enums.auth.TeamRole;
import com.laboschqpa.server.enums.attributeconverter.AuthorityAttributeConverter;
import com.laboschqpa.server.enums.attributeconverter.TeamRoleAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "userAcc")
public class UserAcc implements Serializable {
    static final long serialVersionUID = 42L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Convert(converter = TeamRoleAttributeConverter.class)
    @Column(name = "team_role", nullable = false)
    private TeamRole teamRole = TeamRole.NOTHING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;


    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<UserEmailAddress> userEmailAddresses = new HashSet<>();

    @ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
    @JoinTable(name = "granted_authority", joinColumns = @JoinColumn(name = "user_id"), uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "granted_authority"}))
    @Column(name = "granted_authority", nullable = false)
    @Convert(converter = AuthorityAttributeConverter.class)
    private Set<Authority> authorities = new HashSet<>();

    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ExternalAccountDetail> externalAccountDetails = new HashSet<>();

    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<GoogleExternalAccountDetail> googleExternalAccountDetails = new HashSet<>();

    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<GithubExternalAccountDetail> githubExternalAccountDetails = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<UserEmailAddress> getUserEmailAddresses() {
        return userEmailAddresses;
    }

    public boolean equalsById(UserAcc otherUserAcc) {
        return this.getId() != null
                && this.getId().equals(otherUserAcc.getId());
    }

    public Set<GoogleExternalAccountDetail> getGoogleExternalAccountDetails() {
        return googleExternalAccountDetails;
    }

    public Set<GithubExternalAccountDetail> getGithubExternalAccountDetails() {
        return githubExternalAccountDetails;
    }

    public Set<ExternalAccountDetail> getExternalAccountDetails() {
        return externalAccountDetails;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public List<EnumBasedAuthority> getCopyOfAuthorities_AsEnumBasedAuthority() {
        return this.getAuthorities().stream().map(EnumBasedAuthority::new).collect(Collectors.toList());
    }

    public void setAuthorities_FromEnumBasedAuthority(Collection<EnumBasedAuthority> authorities) {
        this.setAuthorities(authorities.stream()
                .map((ga) -> Authority.fromStringValue(ga.getAuthority()))
                .collect(Collectors.toSet())
        );
    }

    public TeamRole getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(TeamRole teamRole) {
        this.teamRole = teamRole;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}