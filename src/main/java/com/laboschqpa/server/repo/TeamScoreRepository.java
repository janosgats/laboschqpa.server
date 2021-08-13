package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.TeamScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TeamScoreRepository extends JpaRepository<TeamScore, Long> {
    @Modifying
    @Query("delete from TeamScore where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    Optional<TeamScore> findByObjectiveIdAndTeamId(Long objectiveId, Long teamId);

    List<TeamScore> findByObjectiveIdInAndTeamId(Collection<Long> objectiveIds, Long teamId);
}
