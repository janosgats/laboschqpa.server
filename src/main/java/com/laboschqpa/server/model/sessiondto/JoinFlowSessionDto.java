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
public class JoinFlowSessionDto implements Serializable {
    public static final String SESSION_ATTRIBUTE_NAME = "joinFlowSessionDto";
    private static final long serialVersionUID = 1L;

    private Long registrationRequestId;
    private boolean currentlyAddingNewLoginMethod = false;

    public void writeToSession(HttpSession httpSession) {
        httpSession.setAttribute(SESSION_ATTRIBUTE_NAME, this);
    }

    public static JoinFlowSessionDto readFromSession(HttpSession httpSession) {
        return (JoinFlowSessionDto) httpSession.getAttribute(SESSION_ATTRIBUTE_NAME);
    }

    public void writeToCurrentSession() {
        writeToSession(SessionHelper.getCurrentSession(true));
    }

    public static JoinFlowSessionDto readFromCurrentSession() {
        HttpSession session = SessionHelper.getCurrentSession(false);
        if (session != null) {
            return readFromSession(session);
        } else {
            return null;
        }
    }
}
