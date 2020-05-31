package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.ugc.UserGeneratedContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserGeneratedContentRepository extends JpaRepository<Objective, Long> {

    @Query(value = "select ugc.id " +
            "from user_generated_content ugc " +
            "join attachment a on ugc.id = a.ugc_id " +
            "where ugc.dtype = :ugcTypeVal AND a.indexed_file_id = :indexedFileId",
            nativeQuery = true)
    List<Long> getIdsForSpecificUgcTypeWithAttachedFileInternal(
            @Param("ugcTypeVal") Integer ugcTypeVal,
            @Param("indexedFileId") Long indexedFileId);

    default List<Long> getIdsForSpecificUgcTypeWithAttachedFile(UserGeneratedContentType userGeneratedContentType, Long indexedFileId) {
        return getIdsForSpecificUgcTypeWithAttachedFileInternal(userGeneratedContentType.getValue(), indexedFileId);
    }
}
