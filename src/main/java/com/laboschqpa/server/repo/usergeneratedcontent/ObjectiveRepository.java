package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    @Modifying
    @Query("delete from Objective where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    @Query("select" +
            " s.objective.id " +
            "from Submission s " +
            "where s.objective.id IN :objectiveIdsToFilter " +
            "   and s.team.id = :teamId")
    Set<Long> filterObjectiveIdsThatHaveAtLeastOneSubmissionByTeam(Collection<Long> objectiveIdsToFilter, Long teamId);

    @Query("select o from Objective o where (:showHiddenObjectives = true or o.isHidden = false)")
    List<Objective> findAll(Boolean showHiddenObjectives);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select o from Objective o where o.id = :id and (:showHiddenObjectives = true  or o.isHidden = false)")
    Optional<Objective> findByIdWithEagerAttachments(Long id, Boolean showHiddenObjectives);

    @EntityGraph(attributePaths = {"attachments", "program"})
    @Query("select o from Objective o " +
            " where o.objectiveType in :objectiveTypes and (:showHiddenObjectives = true  or o.isHidden = false) " +
            " order by o.creationTime desc")
    List<Objective> findAllByObjectiveType_OrderByCreationTimeDesc_withEagerAttachmentsAndProgram(@Param("objectiveTypes") Collection<ObjectiveType> objectiveTypes,
                                                                                                  Boolean showHiddenObjectives);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select o from Objective o where o.program.id = :programId and (:showHiddenObjectives = true  or o.isHidden = false)")
    List<Objective> findAllByProgramIdWithEagerAttachments(Long programId, Boolean showHiddenObjectives);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select o from Objective o" +
            " where o.program.id = :programId" +
            "    and o.objectiveType = :objectiveType" +
            "    and (:showHiddenObjectives = true  or o.isHidden = false)")
    List<Objective> findAllByProgramIdAndObjectiveTypeWithEagerAttachments(Long programId, ObjectiveType objectiveType, Boolean showHiddenObjectives);
}
