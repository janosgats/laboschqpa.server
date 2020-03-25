package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select team from Team team where team.id = :id and team.archived = false")
    Optional<Team> findByIdAndArchivedIsFalse_WithPessimisticWriteLock(long id);
}