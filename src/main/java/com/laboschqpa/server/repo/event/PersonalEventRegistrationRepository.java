package com.laboschqpa.server.repo.event;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.event.Event;
import com.laboschqpa.server.entity.event.PersonalEventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PersonalEventRegistrationRepository extends JpaRepository<PersonalEventRegistration, Long> {

    Integer countByEvent(Event event);

    Optional<PersonalEventRegistration> findByUserAccAndEvent(UserAcc userAcc, Event event);

    @Transactional
    @Modifying
    @Query("delete from PersonalEventRegistration where userAcc = :userAcc and event = :event")
    int deleteByUserAccAndEvent_andGetDeletedRowCount(UserAcc userAcc, Event event);
}
