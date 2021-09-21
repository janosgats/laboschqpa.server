package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.ratelimit.TeamRateControlEvent;
import com.laboschqpa.server.enums.TeamRateControlTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface TeamRateControlEventRepository extends JpaRepository<TeamRateControlEvent, Long> {
    @Query("" +
            " select count(trce) " +
            " from TeamRateControlEvent trce " +
            " where " +
            "     trce.topic = :topic " +
            "     and trce.teamId = :teamId" +
            "     and trce.time > :sinceTime")
    long countOfEventsSince(TeamRateControlTopic topic, Long teamId, Instant sinceTime);
}