package com.lautarorisso.eCommerce_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  long countByRole(Role role);

  @Query(value = "SELECT * FROM users WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
  Optional<UserEntity> findDeletedById(@Param("id") Long id);

}
