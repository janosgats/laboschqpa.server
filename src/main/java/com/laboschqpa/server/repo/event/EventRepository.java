package com.laboschqpa.server.repo.event;

import com.laboschqpa.server.entity.event.Event;
import com.laboschqpa.server.repo.event.dto.PersonalEventForUserJpaDto;
import com.laboschqpa.server.repo.event.dto.TeamEventForUserJpaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e.id as id, e.target as target, e.name as name, e.registrationDeadline as registrationDeadline, " +
            " e.registrationLimit as registrationLimit, per.id as personalEventRegistrationId " +
            " from Event e " +
            " left join fetch PersonalEventRegistration per on e = per.event and per.userAcc.id = :userId" +
            " where e.target = com.laboschqpa.server.enums.event.EventTarget.PERSONAL")
    List<PersonalEventForUserJpaDto> findAllPersonalEventsForUser(@Param("userId") Long userId);

    @Query("select e.id as id, e.target as target, e.name as name, e.registrationDeadline as registrationDeadline, " +
            " e.registrationLimit as registrationLimit, ter.id as teamEventRegistrationId " +
            " from Event e " +
            " left join fetch TeamEventRegistration ter on e = ter.event and ter.team.id = :teamId" +
            " where e.target = com.laboschqpa.server.enums.event.EventTarget.TEAM")
    List<TeamEventForUserJpaDto> findAllTeamEventsForUser(@Param("teamId") Long teamId);
}
