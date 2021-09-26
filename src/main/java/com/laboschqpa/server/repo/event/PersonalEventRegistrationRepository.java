package com.laboschqpa.server.repo.event;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.event.Event;
import com.laboschqpa.server.entity.event.PersonalEventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PersonalEventRegistrationRepository extends JpaRepository<PersonalEventRegistration, Long> {

    Integer countByEvent(Event event);

    Optional<PersonalEventRegistration> findByUserAndEvent(UserAcc userAcc, Event event);

    @Transactional
    @Modifying
    @Query("delete from PersonalEventRegistration where user = :userAcc and event = :event")
    int deleteByUserAndEvent_andGetDeletedRowCount(UserAcc userAcc, Event event);

    @Query("select ua from UserAcc ua " +
            "left join fetch ua.team " +
            "join PersonalEventRegistration reg on reg.user = ua " +
            "where reg.event.id = :eventId")
    List<UserAcc> findAllRegisteredUsers(@Param("eventId") Long eventId);
}
