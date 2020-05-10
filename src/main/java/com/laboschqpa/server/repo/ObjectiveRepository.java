package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    @Modifying
    @Query("delete from Objective where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);
}
