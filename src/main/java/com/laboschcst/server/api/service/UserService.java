package com.laboschcst.server.api.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.laboschcst.server.entity.ProfileDetails;
import com.laboschcst.server.repo.Repos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    Repos repos;

    public JsonObject getProfileDetails(Long userAccId) {
        Optional<ProfileDetails> profileDetailsOptional = repos.profileDetailsRepository.findByUserAccId(userAccId);

        if (profileDetailsOptional.isEmpty())
            throw new RuntimeException("Cannot find ProfileDetails for userAccId: " + userAccId);

        ProfileDetails profileDetails = profileDetailsOptional.get();
        JsonObject resultJsonObject = new JsonObject();

        resultJsonObject.add("userAccId", new JsonPrimitive(profileDetails.getUserAcc().getId()));
        resultJsonObject.add("firstName", new JsonPrimitive(profileDetails.getFirstName()));
        resultJsonObject.add("lastName", new JsonPrimitive(profileDetails.getLastName()));
        resultJsonObject.add("nickName", new JsonPrimitive(profileDetails.getNickName()));

        return resultJsonObject;

    }
}
