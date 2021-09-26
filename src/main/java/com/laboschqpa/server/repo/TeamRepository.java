package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.repo.dto.TeamMemberJpaDto;
import com.laboschqpa.server.repo.dto.TeamWithScoreJpaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select team from Team team where team.id = :id and team.archived = false")
    Optional<Team> findByIdAndArchivedIsFalse_WithPessimisticWriteLock(long id);

    Optional<Team> findByName(String name);

    @Query("select t.id as id, t.name as name, t.archived as archived, sum(coalesce(ts.score, 0)) as score " +
            " from Team t " +
            "   left join TeamScore ts on ts.team.id = t.id " +
            " where t.archived = false " +
            " group by t " +
            " order by score desc")
    List<TeamWithScoreJpaDto> findAllWithScoreByArchivedIsFalseOrderByScoreDesc();

    @Query("select u.id as userId, u.firstName as firstName, u.lastName as lastName, " +
            " u.nickName as nickName, u.teamRole as teamRole, u.profilePicUrl as profilePicUrl " +
            " from UserAcc u " +
            " where u.team.id = :teamId " +
            "   and u.teamRole in (com.laboschqpa.server.enums.TeamRole.MEMBER, com.laboschqpa.server.enums.TeamRole.LEADER) " +
            " order by u.teamRole desc")
    List<TeamMemberJpaDto> findAllMembers(@Param("teamId") long teamId);

    @Query("select u.id as userId, u.firstName as firstName, u.lastName as lastName, u.nickName as nickName, " +
            " u.teamRole as teamRole, u.profilePicUrl as profilePicUrl " +
            " from UserAcc u " +
            " where u.team.id = :teamId " +
            "   and u.teamRole = com.laboschqpa.server.enums.TeamRole.APPLICANT " +
            " order by u.teamRole desc")
    List<TeamMemberJpaDto> findAllApplicants(@Param("teamId") long teamId);
}