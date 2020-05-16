package com.laboschqpa.server.config.filterchain.extension;

import com.laboschqpa.server.util.ServletHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final CsrfTokenRepository csrfTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        handleSuccess(request, response, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        handleSuccess(request, response, authentication);
    }

    private void handleSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoginSuccessResultDto loginSuccessResultDto = LoginSuccessResultDto.builder()
                .sessionId(encodeToBase64(getCurrentSessionId()))
                .csrfToken(getCurrentCsrfToken(request, response).getToken())
                .build();

        ServletHelper.setJsonResponse(response, loginSuccessResultDto, 200);
    }

    private static String getCurrentSessionId() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        return session.getId();
    }

    private static String encodeToBase64(String source) {
        return new String(Base64Utils.encode(source.getBytes()));
    }

    private CsrfToken getCurrentCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
        if (csrfToken == null) {
            csrfToken = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(csrfToken, request, response);
        }
        return csrfToken;
    }
}
