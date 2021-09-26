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
        final String oldUrl = userAccEntity.getProfilePicUrl();

        if (StringUtils.isBlank(possibleNewUrl)) {
            return;
        }

        if (StringUtils.isBlank(oldUrl)) {
            doUpdatePicture(userAccEntity, possibleNewUrl);
            return;
        }

        if (StringUtils.contains(possibleNewUrl, "googleusercontent.com/a-/")) {
            //The new image is (likely) NOT an auto generated default image, but a real picture, set by the user.
            doUpdatePicture(userAccEntity, possibleNewUrl);
        }

        if (StringUtils.contains(oldUrl, "googleusercontent.com/a/")) {
            //The old image is (likely) an auto generated default image
            doUpdatePicture(userAccEntity, possibleNewUrl);
        }
    }

    private void doUpdatePicture(UserAcc userAccEntity, String possibleNewUrl) {
        if (StringUtils.equals(userAccEntity.getProfilePicUrl(), possibleNewUrl)) {
            return;//No need to update
        }

        userAccEntity.setProfilePicUrl(possibleNewUrl);
        userAccRepository.save(userAccEntity);
    }
}
