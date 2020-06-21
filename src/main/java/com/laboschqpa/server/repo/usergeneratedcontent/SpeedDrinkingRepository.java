package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.SpeedDrinking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpeedDrinkingRepository extends JpaRepository<SpeedDrinking, Long> {
    List<SpeedDrinking> findAllByOrderByCreationTimeDesc();

    @Modifying
    @Query("delete from SpeedDrinking where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select np from SpeedDrinking np where np.id = :id")
    Optional<SpeedDrinking> findByIdWithEagerAttachments(Long id);
}
