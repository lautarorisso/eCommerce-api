package com.lautarorisso.eCommerce_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "categories")
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  public CategoryEntity(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name cannot be empty");
    }
    this.name = name;
  }

  public void changeName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name cannot be empty");
    }
    this.name = name;
  }
}
