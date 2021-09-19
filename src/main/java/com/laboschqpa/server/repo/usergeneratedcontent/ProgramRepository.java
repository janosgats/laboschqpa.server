package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.Program;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetProgramWithTeamScoreJpaDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findAllByOrderByStartTimeAsc();

    @Modifying
    @Query("delete from Program where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select p from Program p where p.id = :id")
    Optional<Program> findByIdWithEagerAttachments(Long id);

    @Query("select coalesce(sum(ts.score), 0) as teamScore" +
            " from Program p " +
            " left join Objective o on o.program = p " +
            " left join TeamScore ts on ts.objective = o and ts.team.id = :teamId " +
            " where p.id = :programId")
    Integer getTeamScore(Long programId, Long teamId);

    /**
     * nativeQuery is used because the projection problem of Timestamp vs. Instant.
     * The current UGC parent JpaDto uses Timestamp which only works with nativeQuery. (2021.09.18.)
     */
        @Query(value = "select " +
            " p.id as id, " +
            " ugc.creator_user_id as creatorUserId, " +
            " ugc.editor_user_id as editorUserId, " +
            " ugc.creation_time as creationTime, " +
            " ugc.edit_time as editTime, " +
            //
            " p.title as title, " +
            " p.headline as headline, " +
            " p.description as description, " +
            " p.start_time as startTime, " +
            " p.end_time as endTime, " +
            //
            " coalesce(sum(ts.score), 0) as teamScore" +
            //
            " from program p " +
            " join user_generated_content ugc on p.id = ugc.id " +
            " left join objective o on o.program_id = p.id " +
            " left join team_score ts on ts.objective_id = o.id and ts.team_id = :teamId " +
            " group by p.id," +
            "        ugc.creator_user_id, ugc.editor_user_id, ugc.creation_time, ugc.edit_time," +
            "        p.title, p.headline, p.description, p.start_time, p.end_time " +
            " order by p.start_time ASC", nativeQuery = true)
    List<GetProgramWithTeamScoreJpaDto> findAll_withTeamScore_orderByStartTimeAsc(Long teamId);

}
