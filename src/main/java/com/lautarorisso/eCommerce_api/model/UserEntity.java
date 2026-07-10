package com.lautarorisso.eCommerce_api.model;

import java.util.regex.Pattern;

import com.lautarorisso.eCommerce_api.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class UserEntity {
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String username;
  @Column(nullable = false, unique = true)
  private String email;
  @Column(nullable = false)
  private String password;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  public UserEntity(String username, String email, String password, Role role) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }

    if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
      throw new IllegalArgumentException("Invalid email");
    }

    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }

    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  public void changeUsername(String newUsername) {

    if (newUsername == null || newUsername.isBlank()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }

    this.username = newUsername;
  }

  public void changeEmail(String newEmail) {

    if (newEmail == null || newEmail.isBlank()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }

    if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
      throw new IllegalArgumentException("Invalid email");
    }

    this.email = newEmail;
  }
}
