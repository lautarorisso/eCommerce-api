package com.lautarorisso.eCommerce_api.security;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.expiration-hours:24}")
  private int expirationHours;

  private SecretKey key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String createToken(Long userId, String email, String role) {
    Date now = new Date();
    return Jwts.builder()
        .subject(email)
        .claim("userId", userId)
        .claim("role", role)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expirationHours * 3600_000L))
        .signWith(key)
        .compact();
  }

  public String extractEmail(String token) {
    return getClaims(token).getSubject();
  }

  public List<String> extractRoles(String token) {
    return List.of(getClaims(token).get("role", String.class));
  }

  public boolean isValid(String token) {
    try {
      getClaims(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
