package com.lautarorisso.eCommerce_api.security;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
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

  @Value("${rate.limit.login.max-attempts:5}")
  private int loginMaxAttempts = 5;
  @Value("${rate.limit.login.window-seconds:60}")
  private long loginWindowSeconds = 60;

  @Value("${rate.limit.register.max-attempts:5}")
  private int registerMaxAttempts = 5;
  @Value("${rate.limit.register.window-seconds:60}")
  private long registerWindowSeconds = 60;

  @Value("${rate.limit.order.max-attempts:10}")
  private int orderMaxAttempts = 10;
  @Value("${rate.limit.order.window-seconds:60}")
  private long orderWindowSeconds = 60;

  @Value("${rate.limit.restock.max-attempts:10}")
  private int restockMaxAttempts = 10;
  @Value("${rate.limit.restock.window-seconds:60}")
  private long restockWindowSeconds = 60;

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
        return new LimitRule("login", loginMaxAttempts, loginWindowSeconds * 1000,
            "Too many login attempts. Please try again later.");
      }
      if ("/api/auth/register".equals(uri)) {
        return new LimitRule("register", registerMaxAttempts, registerWindowSeconds * 1000,
            "Too many registration attempts. Please try again later.");
      }
      if ("/api/orders".equals(uri)) {
        return new LimitRule("order", orderMaxAttempts, orderWindowSeconds * 1000,
            "Too many order attempts. Please try again later.");
      }
    }
    if ("PATCH".equalsIgnoreCase(method) && PRODUCT_RESTOCK.matcher(uri).matches()) {
      return new LimitRule("restock", restockMaxAttempts, restockWindowSeconds * 1000,
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
