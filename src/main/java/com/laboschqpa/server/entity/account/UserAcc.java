package com.laboschqpa.server.entity.account;

import com.laboschqpa.server.config.helper.EnumBasedAuthority;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.entity.account.externalaccountdetail.GithubExternalAccountDetail;
import com.laboschqpa.server.entity.account.externalaccountdetail.GoogleExternalAccountDetail;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.converter.attributeconverter.AuthorityAttributeConverter;
import com.laboschqpa.server.enums.converter.attributeconverter.TeamRoleAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_acc",
        indexes = {
                @Index(columnList = "nick_name", name = "nick_name"),
                @Index(columnList = "team_id, team_role", name = "team_id__team_role"),
                @Index(columnList = "team_role", name = "team_role")
        }
)
public class UserAcc implements Serializable {
    static final long serialVersionUID = 42L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    /**
     * Users are not allowed to see submissions if they are not accepted by e-mail.
     * This flag should be calculated based on the set of accepted e-mails.
     */
    @Column(name = "is_accepted_by_email", nullable = false)
    private Boolean isAcceptedByEmail;

    @Builder.Default
    @Convert(converter = TeamRoleAttributeConverter.class)
    @Column(name = "team_role", nullable = false)
    private TeamRole teamRole = TeamRole.NOTHING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "nick_name")
    private String nickName;

    @Builder.Default
    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<UserEmailAddress> userEmailAddresses = new HashSet<>();

    @Builder.Default
    @ElementCollection(targetClass = Authority.class, fetch = FetchType.LAZY)
    @JoinTable(name = "granted_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            indexes = @Index(columnList = "granted_authority", name = "granted_authority")
    )
    @Column(name = "granted_authority", nullable = false)
    @Convert(converter = AuthorityAttributeConverter.class)
    private Set<Authority> authorities = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ExternalAccountDetail> externalAccountDetails = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<GoogleExternalAccountDetail> googleExternalAccountDetails = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "userAcc", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<GithubExternalAccountDetail> githubExternalAccountDetails = new HashSet<>();

    public Set<UserEmailAddress> getUserEmailAddresses() {
        return userEmailAddresses;
    }

    public boolean equalsById(UserAcc otherUserAcc) {
        return this.getId() != null
                && this.getId().equals(otherUserAcc.getId());
    }

    public Set<EnumBasedAuthority> getCopyOfAuthorities_AsEnumBasedAuthority() {
        return this.getAuthorities().stream().map(EnumBasedAuthority::new).collect(Collectors.toSet());
    }

    public void setAuthorities_FromEnumBasedAuthority(Collection<EnumBasedAuthority> authorities) {
        this.setAuthorities(authorities.stream()
                .map((ga) -> Authority.fromStringValue(ga.getAuthority()))
                .collect(Collectors.toSet())
        );
    }

    public boolean isMemberOrLeaderOfAnyTeam() {
        return team != null && teamRole.isMemberOrLeader();
    }

    public boolean isLeaderOfTeam(long teamId) {
        return team != null && team.getId().equals(teamId) && teamRole == TeamRole.LEADER;
    }
}