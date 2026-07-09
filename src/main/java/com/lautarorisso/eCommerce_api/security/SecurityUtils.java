package com.lautarorisso.eCommerce_api.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

  public CurrentUser getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || !(auth.getDetails() instanceof CurrentUser)) {
      throw new AccessDeniedException("Authentication required");
    }
    return (CurrentUser) auth.getDetails();
  }

  public Long getCurrentUserId() {
    return getCurrentUser().id();
  }

  public boolean isAdmin() {
    return "ADMIN".equals(getCurrentUser().role());
  }
}
