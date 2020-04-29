package com.laboschqpa.server.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

@Log4j2
public class SessionHelper {
    public static HttpSession getCurrentSession(boolean create) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(create);
    }

    public static void invalidateCurrentSession() {
        HttpSession session = getCurrentSession(false);
        if (session != null) {
            session.invalidate();
            log.trace("Session was invalidated.");
        }
    }
}
