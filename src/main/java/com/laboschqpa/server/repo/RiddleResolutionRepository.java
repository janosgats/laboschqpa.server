package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.RiddleResolution;
import com.laboschqpa.server.enums.riddle.RiddleResolutionStatusValues;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetRiddleFirstSolutionJpaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RiddleResolutionRepository extends JpaRepository<RiddleResolution, Long> {
    Optional<RiddleResolution> findByRiddleIdAndTeamId(Long riddleId, Long teamId);

    @Query(value = "" +
            "select t.id as teamId, t.name as teamName, resol.solving_time as solvingTime\n" +
            "from riddle_resolution resol\n" +
            "         join team t on resol.team_id = t.id\n" +
            "where " +
            "resol.riddle_id = :riddleId\n" +
            "  and resol.status = " + RiddleResolutionStatusValues.SOLVED + "\n" +
            "order by resol.solving_time ASC\n" +
            "LIMIT 1",
            nativeQuery = true)
    Optional<GetRiddleFirstSolutionJpaDto> findFirstSolutionOfRiddle(@Param("riddleId") Long riddleId);
}
