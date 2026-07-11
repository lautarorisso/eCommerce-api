package com.lautarorisso.eCommerce_api.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CategoryEntity;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.CategoryRepository;
import com.lautarorisso.eCommerce_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final CartRepository cartRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${admin.email}")
  private String adminEmail;

  @Value("${admin.password}")
  private String adminPassword;

  @Transactional
  @Override
  public void run(String... args) {
    seedAdmin();
    seedCategories();
  }

  private void seedAdmin() {
    if (!userRepository.existsByEmail(adminEmail)) {
      UserEntity admin = new UserEntity("admin", adminEmail,
          passwordEncoder.encode(adminPassword), Role.ADMIN);
      userRepository.save(admin);
      cartRepository.save(new CartEntity(admin));
    }
  }

  private void seedCategories() {
    if (categoryRepository.count() > 0) {
      return;
    }
    List<String> defaultCategories = List.of(
        "Electronics", "Clothing", "Books", "Home & Garden",
        "Sports", "Food & Drinks", "Toys", "Health & Beauty");
    categoryRepository.saveAll(defaultCategories.stream()
        .map(CategoryEntity::new)
        .toList());
  }
}
