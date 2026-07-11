package com.lautarorisso.eCommerce_api.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

class SecurityUtilsTest {

  private final SecurityUtils securityUtils = new SecurityUtils();

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private void setUpAuth(Long userId, String email, String role) {
    var auth = new UsernamePasswordAuthenticationToken(email, null,
        List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    auth.setDetails(new CurrentUser(userId, email, role));
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  @DisplayName("getCurrentUser - should return CurrentUser when authenticated")
  void getCurrentUser_whenAuthenticated_returnsCurrentUser() {
    setUpAuth(1L, "user@example.com", "USER");

    var result = securityUtils.getCurrentUser();

    assertEquals(1L, result.id());
    assertEquals("user@example.com", result.email());
    assertEquals("USER", result.role());
  }

  @Test
  @DisplayName("getCurrentUser - should throw AccessDeniedException when not authenticated")
  void getCurrentUser_whenNotAuthenticated_throwsException() {
    SecurityContextHolder.clearContext();

    assertThrows(AccessDeniedException.class, () -> securityUtils.getCurrentUser());
  }

  @Test
  @DisplayName("isAdmin - should return true when role is ADMIN")
  void isAdmin_withAdminRole_returnsTrue() {
    setUpAuth(1L, "admin@example.com", "ADMIN");

    assertTrue(securityUtils.isAdmin());
  }

  @Test
  @DisplayName("isAdmin - should return false when role is USER")
  void isAdmin_withUserRole_returnsFalse() {
    setUpAuth(1L, "user@example.com", "USER");

    assertFalse(securityUtils.isAdmin());
  }

  @Test
  @DisplayName("assertOwnerOrAdmin - should pass when user is the owner")
  void assertOwnerOrAdmin_whenOwner_doesNotThrow() {
    setUpAuth(1L, "user@example.com", "USER");

    assertDoesNotThrow(() -> securityUtils.assertOwnerOrAdmin(1L));
  }

  @Test
  @DisplayName("assertOwnerOrAdmin - should pass when user is admin")
  void assertOwnerOrAdmin_whenAdmin_doesNotThrow() {
    setUpAuth(1L, "admin@example.com", "ADMIN");

    assertDoesNotThrow(() -> securityUtils.assertOwnerOrAdmin(999L));
  }

  @Test
  @DisplayName("assertOwnerOrAdmin - should throw when not owner and not admin")
  void assertOwnerOrAdmin_whenNotOwnerAndNotAdmin_throws() {
    setUpAuth(1L, "user@example.com", "USER");

    assertThrows(AccessDeniedException.class, () -> securityUtils.assertOwnerOrAdmin(999L));
  }

  @Test
  @DisplayName("assertAdmin - should pass when role is ADMIN")
  void assertAdmin_whenAdmin_doesNotThrow() {
    setUpAuth(1L, "admin@example.com", "ADMIN");

    assertDoesNotThrow(() -> securityUtils.assertAdmin());
  }

  @Test
  @DisplayName("assertAdmin - should throw when role is USER")
  void assertAdmin_whenUser_throws() {
    setUpAuth(1L, "user@example.com", "USER");

    assertThrows(AccessDeniedException.class, () -> securityUtils.assertAdmin());
  }
}
