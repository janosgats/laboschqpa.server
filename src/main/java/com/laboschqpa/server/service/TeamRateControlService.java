package com.laboschqpa.server.service;

import com.laboschqpa.server.entity.ratelimit.TeamRateControlEvent;
import com.laboschqpa.server.enums.TeamRateControlTopic;
import com.laboschqpa.server.exceptions.NotImplementedException;
import com.laboschqpa.server.repo.TeamRateControlEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Log4j2
@RequiredArgsConstructor
@Service
public class TeamRateControlService {
    private static final String METRIC_NAME_TEAM_RATE_CONTROL_EVENT_COUNT = "team_rate_control_event_count";
    private static final String TAG_NAME_TOPIC = "topic";

    private static final String METRIC_NAME_TEAM_RATE_CONTROL_LIMIT_HIT_COUNT = "team_rate_control_limit_hit_count";
    private static final String TAG_NAME_RATE_LIMIT_WINDOW = "rateLimitWindow";
    private static final String TAG_VALUE_TEN_MINUTELY = "tenMinutely";
    private static final String TAG_VALUE_HOURLY = "hourly";
    private static final String TAG_VALUE_DAILY = "daily";

    private static final long SECONDS_IN_MINUTE = 60;
    private static final long SECONDS_IN_TEN_MINUTES = 10 * SECONDS_IN_MINUTE;
    private static final long SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE;
    private static final long SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR;

    private final MeterRegistry meterRegistry;
    private final TeamRateControlEventRepository teamRateControlEventRepository;

    @Value("${teamRateLimit.qrFightTagSubmission.limit.tenMinutely}")
    private Long qrFightTagSubmissionLimitTenMinutely;
    @Value("${teamRateLimit.qrFightTagSubmission.limit.hourly}")
    private Long qrFightTagSubmissionLimitHourly;
    @Value("${teamRateLimit.qrFightTagSubmission.limit.daily}")
    private Long qrFightTagSubmissionLimitDaily;

    public void log(TeamRateControlTopic topic, long teamId) {
        TeamRateControlEvent teamRateControlEvent = new TeamRateControlEvent();

        teamRateControlEvent.setTopic(topic);
        teamRateControlEvent.setTeamId(teamId);
        teamRateControlEvent.setTime(Instant.now());

        teamRateControlEventRepository.save(teamRateControlEvent);
        log.trace("Saved TeamRateControlEvent: id: {}, topic: {}, teamId: {}",
                teamRateControlEvent.getId(), topic, teamId);
        meterRegistry.counter(METRIC_NAME_TEAM_RATE_CONTROL_EVENT_COUNT,
                TAG_NAME_TOPIC, topic.getPrometheusKey()).increment();
    }

    public boolean isRateLimitAlright(TeamRateControlTopic topic, long teamId) {
        if (topic != TeamRateControlTopic.QR_FIGHT_TAG_SUBMISSION_TRIAL) {
            throw new NotImplementedException("Multiple topics are not yet implemented.");
        }

        final Instant now = Instant.now();
        final Instant sinceTimeForTenMinutely = now.minusSeconds(SECONDS_IN_TEN_MINUTES);
        final Instant sinceTimeForHourly = now.minusSeconds(SECONDS_IN_HOUR);
        final Instant sinceTimeForDaily = now.minusSeconds(SECONDS_IN_DAY);


        final long tenMinutelyCount = teamRateControlEventRepository.countOfEventsSince(
                TeamRateControlTopic.QR_FIGHT_TAG_SUBMISSION_TRIAL,
                teamId, sinceTimeForTenMinutely);
        final long hourlyCount = teamRateControlEventRepository.countOfEventsSince(
                TeamRateControlTopic.QR_FIGHT_TAG_SUBMISSION_TRIAL,
                teamId, sinceTimeForHourly);
        final long dailyCount = teamRateControlEventRepository.countOfEventsSince(
                TeamRateControlTopic.QR_FIGHT_TAG_SUBMISSION_TRIAL,
                teamId, sinceTimeForDaily);

        final boolean isOkayTenMinutely = tenMinutelyCount < qrFightTagSubmissionLimitTenMinutely;
        final boolean isOkayHourly = hourlyCount < qrFightTagSubmissionLimitHourly;
        final boolean isOkayDaily = dailyCount < qrFightTagSubmissionLimitDaily;

        if (!isOkayTenMinutely) {
            meterRegistry.counter(METRIC_NAME_TEAM_RATE_CONTROL_LIMIT_HIT_COUNT,
                    TAG_NAME_TOPIC, topic.getPrometheusKey(),
                    TAG_NAME_RATE_LIMIT_WINDOW, TAG_VALUE_TEN_MINUTELY).increment();
        }
        if (!isOkayHourly) {
            meterRegistry.counter(METRIC_NAME_TEAM_RATE_CONTROL_LIMIT_HIT_COUNT,
                    TAG_NAME_TOPIC, topic.getPrometheusKey(),
                    TAG_NAME_RATE_LIMIT_WINDOW, TAG_VALUE_HOURLY).increment();
        }
        if (!isOkayDaily) {
            meterRegistry.counter(METRIC_NAME_TEAM_RATE_CONTROL_LIMIT_HIT_COUNT,
                    TAG_NAME_TOPIC, topic.getPrometheusKey(),
                    TAG_NAME_RATE_LIMIT_WINDOW, TAG_VALUE_DAILY).increment();
        }

        return isOkayTenMinutely && isOkayHourly && isOkayDaily;
    }
}
