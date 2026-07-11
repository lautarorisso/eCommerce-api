package com.lautarorisso.eCommerce_api.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class RateLimitInterceptorTest {

  @InjectMocks
  private RateLimitInterceptor rateLimitInterceptor;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    filterChain = mock(FilterChain.class);
    request.setRequestURI("/api/auth/login");
    request.setMethod("POST");
  }

  @Test
  @DisplayName("doFilterInternal - should allow request within rate limit")
  void doFilterInternal_withinLimit_continuesChain() throws ServletException, IOException {
    rateLimitInterceptor.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertEquals(200, response.getStatus());
  }

  @Test
  @DisplayName("doFilterInternal - should block after exceeding rate limit")
  void doFilterInternal_exceedsLimit_returns429() throws ServletException, IOException {
    for (int i = 0; i < 6; i++) {
      rateLimitInterceptor.doFilterInternal(request, response, filterChain);
    }

    assertEquals(429, response.getStatus());
  }

  @Test
  @DisplayName("doFilterInternal - should not rate limit non-login endpoints")
  void doFilterInternal_nonLoginEndpoint_continuesChain() throws ServletException, IOException {
    request.setRequestURI("/api/products");
    request.setMethod("GET");

    rateLimitInterceptor.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertEquals(200, response.getStatus());
  }
}
