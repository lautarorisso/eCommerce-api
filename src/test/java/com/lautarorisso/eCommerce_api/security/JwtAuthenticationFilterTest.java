package com.lautarorisso.eCommerce_api.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private JwtAuthenticationFilter jwtFilter;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("doFilterInternal - should set authentication when token is valid")
  void doFilterInternal_withValidToken_setsAuthentication() throws ServletException, IOException {
    request.addHeader("Authorization", "Bearer valid-token");
    Claims claims = mock(Claims.class);
    when(jwtService.getClaims("valid-token")).thenReturn(claims);
    when(claims.getSubject()).thenReturn("user@example.com");
    when(claims.get("userId", Long.class)).thenReturn(1L);
    when(claims.get("role", String.class)).thenReturn("USER");

    jwtFilter.doFilterInternal(request, response, filterChain);

    var auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals("user@example.com", auth.getPrincipal());
    assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("doFilterInternal - should return 401 when token is invalid")
  void doFilterInternal_withInvalidToken_returns401() throws ServletException, IOException {
    request.addHeader("Authorization", "Bearer bad-token");
    when(jwtService.getClaims("bad-token")).thenThrow(new JwtException("expired"));

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertEquals(401, response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  @DisplayName("doFilterInternal - should continue chain when no Authorization header")
  void doFilterInternal_withNoHeader_continuesChain() throws ServletException, IOException {
    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
