package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface UserAccRepository extends JpaRepository<UserAcc, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select userAcc " +
            "from UserAcc userAcc " +
            "where " +
            "    userAcc.id = :id " +
            "    and userAcc.enabled = true")
    Optional<UserAcc> findByIdAndEnabledIsTrue_WithPessimisticWriteLock(long id);

    @Query("select count(userAcc.id) " +
            "from UserAcc userAcc " +
            "where " +
            "    userAcc.team = :team " +
            "    and userAcc.teamRole = com.laboschqpa.server.enums.TeamRole.LEADER " +
            "    and userAcc.enabled = true ")
    Integer getCountOfEnabledLeadersInTeam(Team team);

    @Modifying
    @Query("update UserAcc userAcc " +
            "set " +
            "    userAcc.team = null, " +
            "    userAcc.teamRole = com.laboschqpa.server.enums.TeamRole.NOTHING " +
            "where userAcc.team = :team ")
    void kickEveryoneFromTeam(Team team);

    @Query("select userAcc " +
            "from UserAcc userAcc " +
            " left join fetch userAcc.authorities a " +
            "where " +
            "    userAcc.id = :id ")
    Optional<UserAcc> findByIdWithAuthorities(long id);

    @Query("select userAcc " +
            "from UserAcc userAcc " +
            " left join fetch userAcc.authorities a " +
            " left join fetch userAcc.team t " +
            "where " +
            "    userAcc.id = :id ")
    Optional<UserAcc> findByIdWithAuthoritiesAndTeam(long id);

    @Query("select userAcc " +
            "from UserAcc userAcc " +
            " left join fetch userAcc.team t " +
            "where " +
            "    userAcc.id = :id ")
    Optional<UserAcc> findByIdWithTeam(long id);

    @Query("select userAcc " +
            "from UserAcc userAcc " +
            " left join fetch userAcc.team t " +
            "where userAcc.enabled = TRUE " +
            "order by " +
            "t.name ASC NULLS LAST, " +
            "userAcc.nickName ASC NULLS LAST, " +
            "userAcc.firstName ASC NULLS LAST, " +
            "userAcc.lastName ASC NULLS LAST")
    List<UserAcc> findAllEnabled_withTeam_orderByNamesAsc();
}
