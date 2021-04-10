package com.laboschqpa.server.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ProfilePicHelper {
    private static final String AVATAR_API_BASE_URL = "https://eu.ui-avatars.com/api/";

    public static String getAvatarUrl(String firstName, String lastName, String nickName) {
        final String name;
        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
            name = urlEncode(firstName) + "+" + urlEncode(lastName);
        } else if (StringUtils.isNotBlank(nickName)) {
            name = urlEncode(nickName);
        } else {
            name = "Anonymous";
        }

        return AVATAR_API_BASE_URL + "?background=random&name=" + name;
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot URLEncode", e);
        }
    }
}
