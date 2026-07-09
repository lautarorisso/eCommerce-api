package com.lautarorisso.eCommerce_api.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }
    String token = header.substring(7);
    if (!jwtService.isValid(token)) {
      chain.doFilter(request, response);
      return;
    }
    String email = jwtService.extractEmail(token);
    Long userId = jwtService.extractUserId(token);
    String role = jwtService.extractRoles(token).get(0);
    var userDetails = userDetailsService.loadUserByUsername(email);
    var authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    authentication.setDetails(new CurrentUser(userId, email, role));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(request, response);
  }
}
