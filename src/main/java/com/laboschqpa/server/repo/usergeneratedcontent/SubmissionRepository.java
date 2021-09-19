package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Modifying
    @Query("delete from Submission where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select s from Submission s where s.id = :id")
    Optional<Submission> findByIdWithEagerAttachments(Long id);

    @EntityGraph(attributePaths = {"attachments", "objective", "team"})
    @Query("select s from Submission s where s.id = :id")
    Optional<Submission> findByIdWithEagerDisplayEntities(Long id);

    @EntityGraph(attributePaths = {"objective"})
    @Query("select s from Submission s where s.id IN :ids")
    List<Submission> findByIdIn_withObjective(@Param("ids") Collection<Long> ids);

    @EntityGraph(attributePaths = {"attachments", "objective", "team"})
    @Query("select s from Submission s order by s.creationTime desc")
    List<Submission> findAll_withEagerDisplayEntities_orderByCreationTimeDesc();

    @EntityGraph(attributePaths = {"attachments", "objective", "team"})
    @Query("select s from Submission s " +
            " where s.team.id = :teamId " +
            " order by s.creationTime desc")
    List<Submission> findByTeamId_withEagerDisplayEntities_orderByCreationTimeDesc(@Param("teamId") Long teamId);

    @EntityGraph(attributePaths = {"attachments", "objective", "team"})
    @Query("select s from Submission s " +
            " where s.objective.id = :objectiveId " +
            " order by s.creationTime desc")
    List<Submission> findByObjectiveId_withEagerDisplayEntities_orderByCreationTimeDesc(@Param("objectiveId") Long objectiveId);

    @EntityGraph(attributePaths = {"attachments", "objective", "team"})
    @Query("select s from Submission s " +
            " where s.objective.id = :objectiveId " +
            "   and s.team.id = :teamId " +
            " order by s.creationTime desc")
    List<Submission> findByObjectiveIdAndTeamId_withEagerDisplayEntities_orderByCreationTimeDesc(@Param("objectiveId") Long objectiveId, @Param("teamId") Long teamId);
}
