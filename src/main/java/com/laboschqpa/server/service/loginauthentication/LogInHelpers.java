package com.laboschqpa.server.service.loginauthentication;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class LogInHelpers {
    private final UserAccRepository userAccRepository;


    public void updateProfilePicUrlIfNeeded(UserAcc userAccEntity, String possibleNewUrl) {
        if (StringUtils.isBlank(userAccEntity.getProfilePicUrl()) && StringUtils.isNotBlank(possibleNewUrl)) {
            userAccEntity.setProfilePicUrl(possibleNewUrl);
            userAccRepository.save(userAccEntity);
        }
    }
}
