package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.riddle.GetAccessibleRiddleDto;
import com.laboschqpa.server.entity.RiddleResolution;
import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import com.laboschqpa.server.enums.apierrordescriptor.RiddleApiError;
import com.laboschqpa.server.enums.riddle.RiddleResolutionStatus;
import com.laboschqpa.server.exceptions.apierrordescriptor.RiddleException;
import com.laboschqpa.server.repo.RiddleResolutionRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.RiddleRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetAccessibleRiddleJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetRiddleFirstSolutionJpaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class RiddleService {
    private final RiddleRepository riddleRepository;
    private final RiddleResolutionRepository riddleResolutionRepository;

    public List<GetAccessibleRiddleJpaDto> listAccessibleRiddleJpaDtos(Long teamId) {
        return riddleRepository.findAccessibleRiddles(teamId);
    }

    public GetAccessibleRiddleDto getOneRiddleToShow(Long teamId, Long riddleIdToShow) {
        final Riddle riddle = getValidAvailableRiddle(teamId, riddleIdToShow);

        //Find if the current team already solved the riddle
        boolean wasHintUsed = false;
        boolean isAlreadySolved = false;
        final Optional<RiddleResolution> resolutionOptional = riddleResolutionRepository.findByRiddleIdAndTeamId(riddle.getId(), teamId);
        if (resolutionOptional.isPresent()) {
            wasHintUsed = resolutionOptional.get().getHintUsed();
            isAlreadySolved = resolutionOptional.get().getStatus() == RiddleResolutionStatus.SOLVED;
        }

        final GetAccessibleRiddleDto getAccessibleRiddleDto = new GetAccessibleRiddleDto(riddle, wasHintUsed, isAlreadySolved, true);
        getAccessibleRiddleDto.setWasHintUsed(wasHintUsed);
        getAccessibleRiddleDto.setIsAlreadySolved(isAlreadySolved);

        //Find the first solving team of this riddle if there is one
        final Optional<GetRiddleFirstSolutionJpaDto> riddleFirstSolutionOptional = riddleResolutionRepository.findFirstSolutionOfRiddle(riddle.getId());
        if (riddleFirstSolutionOptional.isPresent()) {
            getAccessibleRiddleDto.setFirstSolvingTeamId(riddleFirstSolutionOptional.get().getTeamId());
            getAccessibleRiddleDto.setFirstSolvingTeamName(riddleFirstSolutionOptional.get().getTeamName());
            getAccessibleRiddleDto.setFirstSolvingTimestamp(riddleFirstSolutionOptional.get().getSolvingTimestamp());
        }

        return getAccessibleRiddleDto;
    }

    private Riddle getValidAvailableRiddle(Long teamId, Long riddleIdToShow) {
        final Optional<Riddle> riddleOptional = riddleRepository.findByIdWithEagerAttachments(riddleIdToShow);
        if (riddleOptional.isEmpty()) {
            throw new RiddleException(RiddleApiError.RIDDLE_IS_NOT_FOUND);
        }
        final Riddle riddle = riddleOptional.get();

        assertRiddleIsAccessibleForTeam(riddle, teamId);
        return riddle;
    }

    private void assertRiddleIsAccessibleForTeam(Riddle riddle, Long teamId) {
        List<Long> visibleRiddleIds = riddleRepository.findAccessibleRiddleIds(teamId);
        if (!visibleRiddleIds.contains(riddle.getId())) {
            throw new RiddleException(RiddleApiError.REQUESTED_RIDDLE_IS_NOT_YET_ACCESSIBLE_FOR_YOUR_TEAM);
        }
    }
}
