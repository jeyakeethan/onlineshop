package com.example.onlineshop;

import com.example.onlineshop.user.entity.TokenSession;
import com.example.onlineshop.user.entity.User;
import com.example.onlineshop.user.repository.TokenSessionRepository;
import com.example.onlineshop.security.JwtAuthenticationFilter;
import com.example.onlineshop.security.JwtUtil;
import com.example.onlineshop.user.service.UserService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenSessionTest {

    @Mock
    private TokenSessionRepository tokenSessionRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserService filterUserService;

    @Test
    void revokeToken_marksMatchingActiveSessionRevoked() {
        TokenSession session = new TokenSession();
        session.setTokenId("token-id");
        session.setUserEmail("admin@example.com");
        session.setExpiresAt(Instant.now().plusSeconds(3600));

        when(jwtUtil.extractTokenId("jwt-token")).thenReturn("token-id");
        when(jwtUtil.extractUsername("jwt-token")).thenReturn("admin@example.com");
        when(tokenSessionRepository.findByTokenIdAndUserEmailAndRevokedAtIsNull(
                "token-id", "admin@example.com")).thenReturn(Optional.of(session));

        userService.revokeToken("jwt-token");

        assertNotNull(session.getRevokedAt());
        verify(tokenSessionRepository).save(session);
    }

    @Test
    void isTokenActive_checksSessionOwnerAndExpiry() {
        when(jwtUtil.extractTokenId("jwt-token")).thenReturn("token-id");
        when(tokenSessionRepository.existsByTokenIdAndUserEmailAndRevokedAtIsNullAndExpiresAtAfter(
                any(), any(), any())).thenReturn(true);

        org.junit.jupiter.api.Assertions.assertTrue(userService.isTokenActive("jwt-token", "admin@example.com"));
        verify(tokenSessionRepository).existsByTokenIdAndUserEmailAndRevokedAtIsNullAndExpiresAtAfter(
                org.mockito.ArgumentMatchers.eq("token-id"),
                org.mockito.ArgumentMatchers.eq("admin@example.com"), any());
    }

    @Test
    void jwtFilter_rejectsRevokedToken() throws Exception {
        SecurityContextHolder.clearContext();
        User user = new User();
        user.setEmail("admin@example.com");
        user.setRoles(List.of());

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, filterUserService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer revoked-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {};

        when(jwtUtil.extractUsername("revoked-token")).thenReturn("admin@example.com");
        when(filterUserService.getUserDetailById("admin@example.com")).thenReturn(user);
        when(jwtUtil.validateToken("revoked-token", user)).thenReturn(true);
        when(filterUserService.isTokenActive("revoked-token", "admin@example.com")).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(filterUserService).isTokenActive("revoked-token", "admin@example.com");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        org.junit.jupiter.api.Assertions.assertEquals(401, response.getStatus());
        org.junit.jupiter.api.Assertions.assertTrue(response.getContentAsString().contains("Your session has expired. Please log in again."));
    }
}
