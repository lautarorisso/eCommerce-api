package com.lautarorisso.eCommerce_api.security;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor extends OncePerRequestFilter {

  private static final int MAX_ATTEMPTS = 5;
  private static final long WINDOW_MS = 60_000;

  private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
      String clientIp = getClientIp(request);
      long now = System.currentTimeMillis();

      AttemptInfo info = attempts.compute(clientIp, (key, existing) -> {
        if (existing == null || now - existing.windowStart > WINDOW_MS) {
          return new AttemptInfo(now, new AtomicInteger(1));
        }
        existing.count.incrementAndGet();
        return existing;
      });

      if (info.count.get() > MAX_ATTEMPTS) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":429,\"message\":\"Too many login attempts. Please try again later.\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private static class AttemptInfo {
    final long windowStart;
    final AtomicInteger count;

    AttemptInfo(long windowStart, AtomicInteger count) {
      this.windowStart = windowStart;
      this.count = count;
    }
  }
}
