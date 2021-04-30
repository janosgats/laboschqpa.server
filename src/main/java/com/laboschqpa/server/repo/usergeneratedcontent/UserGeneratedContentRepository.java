package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.repo.dto.UserGeneratedContentParentJpaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserGeneratedContentRepository extends JpaRepository<Objective, Long> {

    @Query(value = "select ugc.id as id, ugc.creator_user_id as creatorUserId, ugc.editor_user_id as editorUserId, " +
            "              ugc.creation_time as creationTime, ugc.edit_time as editTime, ugc.dtype as dtype " +
            " from user_generated_content ugc " +
            " join attachment a on ugc.id = a.ugc_id " +
            " where a.indexed_file_id = :indexedFileId",
            nativeQuery = true)
    List<UserGeneratedContentParentJpaDto> getOnlyParentsThatHaveAttachment(@Param("indexedFileId") Long indexedFileId);
}
