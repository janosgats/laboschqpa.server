package com.laboschcst.server.api.service;

import com.laboschcst.server.api.dto.ProfileDetailsDto;
import com.laboschcst.server.entity.ProfileDetails;
import com.laboschcst.server.exceptions.ContentNotFoundApiException;
import com.laboschcst.server.repo.Repos;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    Repos repos;

    public UserService(Repos repos) {
        this.repos = repos;
    }

    public ProfileDetailsDto getProfileDetails(Long userAccId) {
        Optional<ProfileDetails> profileDetailsOptional = repos.profileDetailsRepository.findByUserAccId(userAccId);

        if (profileDetailsOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find ProfileDetails for userAccId: " + userAccId);

        ProfileDetails profileDetails = profileDetailsOptional.get();

        return new ProfileDetailsDto(
                profileDetails.getUserAcc().getId(),
                profileDetails.getFirstName(),
                profileDetails.getLastName(),
                profileDetails.getNickName());
    }
}
