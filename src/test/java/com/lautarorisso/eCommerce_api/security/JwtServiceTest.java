package com.lautarorisso.eCommerce_api.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.JwtException;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "secret", "a-much-longer-secret-key-that-is-32-chars!");
    ReflectionTestUtils.setField(jwtService, "expirationHours", 24);
    jwtService.init();
  }

  @Test
  @DisplayName("createToken - should return a non-null compact JWT string")
  void createToken_returnsNonNullToken() {
    String token = jwtService.createToken(1L, "user@example.com", "USER");

    assertNotNull(token);
    assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts");
  }

  @Test
  @DisplayName("getClaims - should parse a valid token and return correct claims")
  void getClaims_withValidToken_returnsCorrectClaims() {
    String token = jwtService.createToken(42L, "admin@example.com", "ADMIN");

    var claims = jwtService.getClaims(token);

    assertEquals("admin@example.com", claims.getSubject());
    assertEquals(42L, claims.get("userId", Long.class));
    assertEquals("ADMIN", claims.get("role", String.class));
  }

  @Test
  @DisplayName("getClaims - should throw JwtException for a tampered token")
  void getClaims_withTamperedToken_throwsJwtException() {
    String token = jwtService.createToken(1L, "user@example.com", "USER");
    String tampered = token.substring(0, token.length() - 5) + "XXXXX";

    assertThrows(JwtException.class, () -> jwtService.getClaims(tampered));
  }

  @Test
  @DisplayName("getClaims - should throw JwtException for garbage string")
  void getClaims_withGarbageToken_throwsJwtException() {
    assertThrows(JwtException.class, () -> jwtService.getClaims("not.a.valid.token"));
  }
}
