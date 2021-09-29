package com.laboschqpa.server.model.sessiondto;

import com.laboschqpa.server.util.SessionHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegistrationSessionDto implements Serializable {
    public static final String SESSION_ATTRIBUTE_NAME = "registrationSessionDto";
    private static final long serialVersionUID = 1L;

    private String oauth2ProviderRegistrationKey;
    private String oauth2ExternalAccountDetailString;
    private String emailAddress;

    private String firstName;
    private String lastName;
    private String nickName;

    private String profilePicUrl;

    public String getNameDigest() {
        return "_first: \"" + firstName + "\", _last: \"" + lastName + "\", _nick: \"" + nickName + "\"";
    }

    public void writeToSession(HttpSession httpSession) {
        httpSession.setAttribute(SESSION_ATTRIBUTE_NAME, this);
    }

    public void removeFromSession(HttpSession httpSession) {
        httpSession.removeAttribute(SESSION_ATTRIBUTE_NAME);
    }

    public static RegistrationSessionDto readFromSession(HttpSession httpSession) {
        return (RegistrationSessionDto) httpSession.getAttribute(SESSION_ATTRIBUTE_NAME);
    }

    public void writeToCurrentSession() {
        writeToSession(SessionHelper.getCurrentSession(true));
    }

    public void removeFromCurrentSession() {
        HttpSession session = SessionHelper.getCurrentSession(false);
        if (session != null) {
            removeFromSession(session);
        }
    }

    public static RegistrationSessionDto readFromCurrentSession() {
        HttpSession session = SessionHelper.getCurrentSession(false);
        if (session != null) {
            return readFromSession(session);
        } else {
            return null;
        }
    }
}
