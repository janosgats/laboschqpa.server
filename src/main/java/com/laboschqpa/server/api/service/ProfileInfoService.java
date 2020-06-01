package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.profileinfo.EditCurrentProfileInfoDto;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileInfoService {
    private final UserAccRepository userAccRepository;

    public void setCurrentProfileInfo(EditCurrentProfileInfoDto editCurrentProfileInfoDto, UserAcc loggedInUserAcc) {
        loggedInUserAcc.setFirstName(editCurrentProfileInfoDto.getFirstName());
        loggedInUserAcc.setLastName(editCurrentProfileInfoDto.getLastName());
        loggedInUserAcc.setNickName(editCurrentProfileInfoDto.getNickName());

        userAccRepository.save(loggedInUserAcc);
    }
}
