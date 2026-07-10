package com.lautarorisso.eCommerce_api.specification;

import org.springframework.data.jpa.domain.Specification;

import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.model.UserEntity;

public class UserSpecification {

  public static Specification<UserEntity> usernameOrEmailContains(String search) {
    return (root, query, cb) -> {
      if (search == null || search.isBlank()) {
        return null;
      }
      String escaped = search.toLowerCase().replace("%", "\\%").replace("_", "\\_");
      String pattern = "%" + escaped + "%";
      return cb.or(
          cb.like(cb.lower(root.get("username")), pattern),
          cb.like(cb.lower(root.get("email")), pattern));
    };
  }

  public static Specification<UserEntity> roleEquals(Role role) {
    return (root, query, cb) -> {
      if (role == null) {
        return null;
      }
      return cb.equal(root.get("role"), role);
    };
  }
}
