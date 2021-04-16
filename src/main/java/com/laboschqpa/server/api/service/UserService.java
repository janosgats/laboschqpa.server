package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.user.PostSetUserInfoRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserAccRepository userAccRepository;

    public UserAcc getById(long id) {
        return getValidUser(id, false);
    }

    public UserAcc getByIdWithAuthorities(long id) {
        return getValidUser(id, true);
    }

    public void setUserInfo(PostSetUserInfoRequest postSetUserInfoRequest) {
        UserAcc userAcc = getValidUser(postSetUserInfoRequest.getUserId(), false);

        userAcc.setFirstName(postSetUserInfoRequest.getFirstName());
        userAcc.setLastName(postSetUserInfoRequest.getLastName());
        userAcc.setNickName(postSetUserInfoRequest.getNickName());

        userAccRepository.save(userAcc);
    }

    public List<UserAcc> listAll() {
        return userAccRepository.findAll();
    }

    private UserAcc getValidUser(long id, boolean withAuthorities) {
        final Optional<UserAcc> userAccOptional;
        if (withAuthorities) {
            userAccOptional = userAccRepository.findByIdWithAuthorities(id);
        } else {
            userAccOptional = userAccRepository.findById(id);
        }
        return userAccOptional.orElseThrow(() -> new ContentNotFoundException("User with id " + id + " is not found."));
    }
}
