package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface UserAccRepository extends JpaRepository<UserAcc, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select userAcc " +
            "from UserAcc userAcc " +
            "where " +
            "    userAcc.id = :id " +
            "    and userAcc.enabled = true")
    Optional<UserAcc> findByIdAndEnabledIsTrue_WithPessimisticWriteLock(long id);

    Optional<UserAcc> findByIdAndEnabledIsTrue(long id);

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
}
