package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.ObjectiveAcceptance;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ObjectiveAcceptanceRepository extends JpaRepository<ObjectiveAcceptance, Long> {
    @Transactional
    @Modifying
    @Query("delete from ObjectiveAcceptance where objective = :objective and team = :team")
    int deleteByObjectiveAndTeamAndGetDeletedRowCount(Objective objective, Team team);

    Optional<ObjectiveAcceptance> findByObjectiveIdAndTeamId(Long objectiveId, Long teamId);

    List<ObjectiveAcceptance> findByObjectiveIdInAndTeamId(Collection<Long> objectiveIds, Long teamId);

    @Query("select" +
            " oa.objective.id " +
            "from ObjectiveAcceptance oa " +
            "where oa.objective.id IN :objectiveIdsToFilter " +
            "   and oa.team.id = :teamId")
    Set<Long> filterObjectiveIdsThatAreAcceptedForTeam(Collection<Long> objectiveIdsToFilter, Long teamId);
}
