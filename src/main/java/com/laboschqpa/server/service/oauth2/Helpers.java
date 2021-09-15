package com.laboschqpa.server.service.oauth2;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

public class Helpers {

    public static String getNickName(String firstName, String lastName, String emailAddress, String timePrefix) {
        if (StringUtils.isNotBlank(firstName) || StringUtils.isNotBlank(lastName)) {
            return null;
        }

        if (StringUtils.isNotBlank(emailAddress)) {
            final String[] splitAddress = emailAddress.split("@");
            if (splitAddress.length > 0) {
                return splitAddress[0];
            }
        }

        return timePrefix + "-" + Instant.now().toString();
    }
}
