package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.currentuser.GetCsrfTokenResponse;
import com.laboschqpa.server.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUserControllerTest {
    @Mock
    CsrfTokenRepository csrfTokenRepository;
    @Mock
    UserService userService;
    @InjectMocks
    CurrentUserController currentUserController;

    @Test
    void getCsrfToken_alreadyExists() {
        final HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

        final String testTokenString = "testTokenString";
        final CsrfToken csrfToken = createMockCsrfToken(testTokenString);

        when(csrfTokenRepository.loadToken(servletRequest)).thenReturn(csrfToken);

        Mono<GetCsrfTokenResponse> resultingMono = currentUserController.getCsrfToken(servletRequest, servletResponse);
        String resultingString = resultingMono.block().getCsrfToken();

        assertEquals(testTokenString, resultingString);
        verify(csrfTokenRepository, times(1)).loadToken(servletRequest);
        verify(csrfTokenRepository, times(0)).generateToken(any());
        verify(csrfTokenRepository, times(0)).saveToken(any(), any(), any());
    }

    @Test
    void getCsrfToken_notExisting_thenExisting() {
        final HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

        final String testTokenString = "testTokenString";
        final CsrfToken csrfToken = createMockCsrfToken(testTokenString);

        when(csrfTokenRepository.loadToken(servletRequest))
                .thenReturn(null)
                .thenReturn(csrfToken);

        Mono<GetCsrfTokenResponse> resultingMono = currentUserController.getCsrfToken(servletRequest, servletResponse);
        String resultingString = resultingMono.block().getCsrfToken();

        assertEquals(testTokenString, resultingString);

        assertEquals(testTokenString, resultingString);
        verify(csrfTokenRepository, times(2)).loadToken(servletRequest);
        verify(csrfTokenRepository, times(0)).generateToken(any());
        verify(csrfTokenRepository, times(0)).saveToken(any(), any(), any());
    }

    @Test
    void getCsrfToken_notExisting_haveToGenerate() {
        final HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

        final String testTokenString = "testTokenString";
        final CsrfToken csrfTokenToGenerate = createMockCsrfToken(testTokenString);

        when(csrfTokenRepository.generateToken(servletRequest)).thenReturn(csrfTokenToGenerate);

        Mono<GetCsrfTokenResponse> resultingMono = currentUserController.getCsrfToken(servletRequest, servletResponse);
        String resultingString = resultingMono.block().getCsrfToken();

        assertEquals(testTokenString, resultingString);
        verify(csrfTokenRepository, times(2)).loadToken(servletRequest);
        verify(csrfTokenRepository, times(1)).generateToken(servletRequest);
        verify(csrfTokenRepository, times(1)).saveToken(csrfTokenToGenerate, servletRequest, servletResponse);
    }

    private CsrfToken createMockCsrfToken(String tokenString) {
        final CsrfToken csrfTokenToReturn = mock(CsrfToken.class);
        lenient().when(csrfTokenToReturn.getToken()).thenReturn(tokenString);
        return csrfTokenToReturn;
    }
}