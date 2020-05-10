package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Modifying
    @Query("delete from Submission where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);
}
