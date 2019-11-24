package com.laboschcst.server.config.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class ReloadUserPerRequestHttpSessionSecurityContextRepository extends HttpSessionSecurityContextRepository {
    Logger logger = LoggerFactory.getLogger(ReloadUserPerRequestHttpSessionSecurityContextRepository.class);

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        logger.info("asdasdskamdjaskdjsa");
        return super.loadContext(requestResponseHolder);
    }
}
