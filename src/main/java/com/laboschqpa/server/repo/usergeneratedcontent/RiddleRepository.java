package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RiddleRepository extends JpaRepository<Riddle, Long> {
    @Modifying
    @Query("delete from Riddle where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select r from Riddle r where r.id = :id")
    Optional<Riddle> findByIdWithEagerAttachments(Long id);
}
