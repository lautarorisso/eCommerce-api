package com.lautarorisso.eCommerce_api.security;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor extends OncePerRequestFilter {

  private static final Pattern PRODUCT_RESTOCK = Pattern.compile("^/api/products/[^/]+/restock$");

  private static final int LOGIN_MAX_ATTEMPTS = 5;
  private static final int REGISTER_MAX_ATTEMPTS = 5;
  private static final int ORDER_MAX_ATTEMPTS = 10;
  private static final int RESTOCK_MAX_ATTEMPTS = 10;
  private static final long WINDOW_MS = 60_000;

  private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    LimitRule rule = resolveRule(request);
    if (rule != null) {
      String clientIp = getClientIp(request);
      String key = rule.name() + ":" + clientIp;
      long now = System.currentTimeMillis();

      AttemptInfo info = attempts.compute(key, (k, existing) -> {
        if (existing == null || now - existing.windowStart > rule.windowMs()) {
          return new AttemptInfo(now, new AtomicInteger(1));
        }
        existing.count.incrementAndGet();
        return existing;
      });

      if (info.count.get() > rule.maxAttempts()) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"status\":429,\"message\":\"" + rule.message() + "\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private LimitRule resolveRule(HttpServletRequest request) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    if ("POST".equalsIgnoreCase(method)) {
      if ("/api/auth/login".equals(uri)) {
        return new LimitRule("login", LOGIN_MAX_ATTEMPTS, WINDOW_MS,
            "Too many login attempts. Please try again later.");
      }
      if ("/api/auth/register".equals(uri)) {
        return new LimitRule("register", REGISTER_MAX_ATTEMPTS, WINDOW_MS,
            "Too many registration attempts. Please try again later.");
      }
      if ("/api/orders".equals(uri)) {
        return new LimitRule("order", ORDER_MAX_ATTEMPTS, WINDOW_MS,
            "Too many order attempts. Please try again later.");
      }
    }
    if ("PATCH".equalsIgnoreCase(method) && PRODUCT_RESTOCK.matcher(uri).matches()) {
      return new LimitRule("restock", RESTOCK_MAX_ATTEMPTS, WINDOW_MS,
          "Too many restock attempts. Please try again later.");
    }
    return null;
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private record LimitRule(String name, int maxAttempts, long windowMs, String message) {
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
