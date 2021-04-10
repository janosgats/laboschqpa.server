package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.user.PostSetUserInfoRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserAccRepository userAccRepository;

    public UserAcc getById(long id) {
        return getValidUser(id);
    }

    public void setUserInfo(PostSetUserInfoRequest postSetUserInfoRequest) {
        UserAcc userAcc = getValidUser(postSetUserInfoRequest.getUserId());

        userAcc.setFirstName(postSetUserInfoRequest.getFirstName());
        userAcc.setLastName(postSetUserInfoRequest.getLastName());
        userAcc.setNickName(postSetUserInfoRequest.getNickName());

        userAccRepository.save(userAcc);
    }

    private UserAcc getValidUser(long id) {
        return userAccRepository.findById(id).orElseThrow(() -> new ContentNotFoundException("User with id " + id + " is not found."));
    }
}
