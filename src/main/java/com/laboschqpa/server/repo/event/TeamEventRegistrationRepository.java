package com.laboschqpa.server.repo.event;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.event.Event;
import com.laboschqpa.server.entity.event.TeamEventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TeamEventRegistrationRepository extends JpaRepository<TeamEventRegistration, Long> {

    Integer countByEvent(Event event);

    Optional<TeamEventRegistration> findByTeamAndEvent(Team team, Event event);

    @Transactional
    @Modifying
    @Query("delete from TeamEventRegistration where team = :team and event = :event")
    int deleteByTeamAndEvent_andGetDeletedRowCount(Team team, Event event);
}
