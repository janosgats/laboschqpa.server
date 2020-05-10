package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ProfileDetailsDto;
import com.laboschqpa.server.api.validator.ProfileDetailsValidator;
import com.laboschqpa.server.entity.ProfileDetails;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.exceptions.ContentNotFoundApiException;
import com.laboschqpa.server.repo.ProfileDetailsRepository;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final ProfileDetailsRepository profileDetailsRepository;
    private final UserAccRepository userAccRepository;

    public ProfileDetailsDto getProfileDetails(Long userAccId) {
        Optional<ProfileDetails> profileDetailsOptional = profileDetailsRepository.findByUserAccId(userAccId);

        if (profileDetailsOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find ProfileDetails for userAccId: " + userAccId);

        ProfileDetails profileDetails = profileDetailsOptional.get();

        return new ProfileDetailsDto(
                profileDetails.getUserAcc().getId(),
                profileDetails.getFirstName(),
                profileDetails.getLastName(),
                profileDetails.getNickName());
    }

    public void saveProfileDetails(ProfileDetailsDto profileDetailsDto) {
        new ProfileDetailsValidator(profileDetailsDto);

        Optional<UserAcc> userAccOptional = userAccRepository.findById(profileDetailsDto.getUserAccId());

        if (userAccOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find UserAcc with Id: " + profileDetailsDto.getUserAccId());

        Optional<ProfileDetails> profileDetailsOptional = profileDetailsRepository.findByUserAccId(profileDetailsDto.getUserAccId());
        ProfileDetails profileDetails = profileDetailsOptional.orElse(new ProfileDetails());
        profileDetails.setUserAcc(userAccOptional.get());
        profileDetails.setFirstName(profileDetailsDto.getFirstName());
        profileDetails.setLastName(profileDetailsDto.getLastName());
        profileDetails.setNickName(profileDetailsDto.getNickName());

        profileDetailsRepository.save(profileDetails);
    }
}
