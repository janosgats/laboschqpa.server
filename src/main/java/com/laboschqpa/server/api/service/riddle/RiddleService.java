package com.laboschqpa.server.api.service.riddle;

import com.laboschqpa.server.api.dto.ugc.riddle.GetAccessibleRiddleResponse;
import com.laboschqpa.server.api.dto.ugc.riddle.RiddleSubmitSolutionResponse;
import com.laboschqpa.server.entity.RiddleResolution;
import com.laboschqpa.server.entity.Team;
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

import java.time.Instant;
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

    public GetAccessibleRiddleResponse getOneRiddleToShow(Long teamId, Long riddleIdToShow) {
        final Riddle riddle = getExistingAccessibleRiddle(teamId, riddleIdToShow, true);

        //Find if the current team already solved the riddle
        boolean wasHintUsed = false;
        boolean isAlreadySolved = false;
        final Optional<RiddleResolution> resolutionOptional = riddleResolutionRepository.findByRiddleIdAndTeamId(riddle.getId(), teamId);
        if (resolutionOptional.isPresent()) {
            wasHintUsed = resolutionOptional.get().getHintUsed();
            isAlreadySolved = resolutionOptional.get().getStatus() == RiddleResolutionStatus.SOLVED;
        }

        final GetAccessibleRiddleResponse getAccessibleRiddleResponse = new GetAccessibleRiddleResponse(riddle, wasHintUsed, isAlreadySolved, true);
        getAccessibleRiddleResponse.setWasHintUsed(wasHintUsed);
        getAccessibleRiddleResponse.setIsAlreadySolved(isAlreadySolved);

        //Find the first solving team of this riddle if there is one
        final Optional<GetRiddleFirstSolutionJpaDto> riddleFirstSolutionOptional = riddleResolutionRepository.findFirstSolutionOfRiddle(riddle.getId());
        if (riddleFirstSolutionOptional.isPresent()) {
            getAccessibleRiddleResponse.setFirstSolvingTeamId(riddleFirstSolutionOptional.get().getTeamId());
            getAccessibleRiddleResponse.setFirstSolvingTeamName(riddleFirstSolutionOptional.get().getTeamName());
            getAccessibleRiddleResponse.setFirstSolvingTime(riddleFirstSolutionOptional.get().getSolvingTimeAsInstant());
        }

        return getAccessibleRiddleResponse;
    }

    public String useHint(Long teamId, Long riddleIdToUseHintOf) {
        final Riddle riddle = getExistingAccessibleRiddle(teamId, riddleIdToUseHintOf, false);

        final Optional<RiddleResolution> existingResolutionOptional = riddleResolutionRepository.findByRiddleIdAndTeamId(riddle.getId(), teamId);

        if (existingResolutionOptional.isEmpty()) {
            final RiddleResolution riddleResolution = new RiddleResolution();
            riddleResolution.setRiddle(riddle);
            riddleResolution.setTeam(new Team(teamId));
            riddleResolution.setStatus(RiddleResolutionStatus.UNSOLVED);
            riddleResolution.setHintUsed(true);
            riddleResolutionRepository.save(riddleResolution);

            return riddle.getHint();
        } else {
            final RiddleResolution existingResolution = existingResolutionOptional.get();
            if (!existingResolution.getHintUsed()) {
                existingResolution.setHintUsed(true);
                riddleResolutionRepository.save(existingResolution);
            }
            return riddle.getHint();
        }
    }

    public RiddleSubmitSolutionResponse submitSolution(Long teamId, Long riddleIdToSubmitSolutionTo, String givenSolution) {
        final Riddle riddle = getExistingAccessibleRiddle(teamId, riddleIdToSubmitSolutionTo, false);
        final boolean isGivenSolutionCorrect = RiddleHelpers.areSolutionsSimilar(riddle.getSolution(), givenSolution);

        final Optional<RiddleResolution> existingResolutionOptional = riddleResolutionRepository.findByRiddleIdAndTeamId(riddle.getId(), teamId);

        final boolean wasAlreadySolved;
        if (isGivenSolutionCorrect) {
            if (existingResolutionOptional.isPresent()) {
                wasAlreadySolved = existingResolutionOptional.get().getStatus() == RiddleResolutionStatus.SOLVED;

                if (!wasAlreadySolved) {
                    //Updating the existing resolution
                    existingResolutionOptional.get().setStatus(RiddleResolutionStatus.SOLVED);
                    existingResolutionOptional.get().setSolvingTime(Instant.now());
                    riddleResolutionRepository.save(existingResolutionOptional.get());
                }
            } else {
                //Creating new RiddleResolution because the new solution is correct
                wasAlreadySolved = false;

                final RiddleResolution riddleResolution = new RiddleResolution();
                riddleResolution.setRiddle(riddle);
                riddleResolution.setTeam(new Team(teamId));
                riddleResolution.setStatus(RiddleResolutionStatus.SOLVED);
                riddleResolution.setSolvingTime(Instant.now());
                riddleResolution.setHintUsed(false);

                riddleResolutionRepository.save(riddleResolution);
            }
            return new RiddleSubmitSolutionResponse(true, true, wasAlreadySolved);
        } else {
            //The submitted solution is wrong, but we check if the riddle was already solved by the team to put it int the response DTO
            wasAlreadySolved = existingResolutionOptional
                    .filter(riddleResolution ->
                            riddleResolution.getStatus() == RiddleResolutionStatus.SOLVED
                    ).isPresent();

            return new RiddleSubmitSolutionResponse(false, wasAlreadySolved, wasAlreadySolved);
        }
    }

    public Riddle getExistingAccessibleRiddle(Long teamId, Long riddleId, boolean fetchAttachmentsEagerly) {
        final Optional<Riddle> riddleOptional;
        if (fetchAttachmentsEagerly) {
            riddleOptional = riddleRepository.findByIdWithEagerAttachments(riddleId);
        } else {
            riddleOptional = riddleRepository.findById(riddleId);
        }

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
