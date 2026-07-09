package com.lautarorisso.eCommerce_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${admin.email}")
  private String adminEmail;

  @Value("${admin.password}")
  private String adminPassword;

  @Override
  public void run(String... args) {
    if (!userRepository.existsByEmail(adminEmail)) {
      UserEntity admin = new UserEntity("admin", adminEmail,
          passwordEncoder.encode(adminPassword), Role.ADMIN);
      userRepository.save(admin);
    }
  }
}
