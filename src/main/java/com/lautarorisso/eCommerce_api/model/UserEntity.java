package com.lautarorisso.eCommerce_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String username;
  @Column(nullable = false, unique = true)
  private String email;

  public UserEntity(String username, String email) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }

    if (email == null || email.isBlank() || !email.contains("@")) {
      throw new IllegalArgumentException("Invalid email");
    }

    this.username = username;
    this.email = email;
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

    if (!newEmail.contains("@")) {
      throw new IllegalArgumentException("Invalid email");
    }

    this.email = newEmail;
  }
}
