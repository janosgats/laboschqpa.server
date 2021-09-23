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

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    @Modifying
    @Query("delete from Objective where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);


    @Query("select o from Objective o where (:showFractionObjectives = true or o.isHidden = false)")
    List<Objective> findAll(Boolean showFractionObjectives);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select o from Objective o where o.id = :id and (:showFractionObjectives = true  or o.isHidden = false)")
    Optional<Objective> findByIdWithEagerAttachments(Long id, Boolean showFractionObjectives);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select o from Objective o " +
            " where o.objectiveType in :objectiveTypes and (:showFractionObjectives = true  or o.isHidden = false) " +
            " order by o.creationTime desc")
    List<Objective> findAllByObjectiveType_OrderByCreationTimeDesc_withEagerAttachments(@Param("objectiveTypes") Collection<ObjectiveType> objectiveTypes,
                                                                                        Boolean showFractionObjectives);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select o from Objective o where o.program.id = :programId and (:showFractionObjectives = true  or o.isHidden = false)")
    List<Objective> findAllByProgramIdWithEagerAttachments(Long programId, Boolean showFractionObjectives);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select o from Objective o" +
            " where o.program.id = :programId" +
            "    and o.objectiveType = :objectiveType" +
            "    and (:showFractionObjectives = true  or o.isHidden = false)")
    List<Objective> findAllByProgramIdAndObjectiveTypeWithEagerAttachments(Long programId, ObjectiveType objectiveType, Boolean showFractionObjectives);
}
