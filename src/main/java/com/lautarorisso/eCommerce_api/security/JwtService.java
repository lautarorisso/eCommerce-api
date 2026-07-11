package com.lautarorisso.eCommerce_api.security;

import java.util.Date;

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
    byte[] keyBytes = secret.getBytes();
    if (keyBytes.length < 32) {
      throw new IllegalArgumentException(
          "JWT secret must be at least 32 characters long (current: " + keyBytes.length + ")");
    }
    this.key = Keys.hmacShaKeyFor(keyBytes);
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

  public Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
