package com.lautarorisso.eCommerce_api.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lautarorisso.eCommerce_api.exceptions.InsufficientResourcesException;

class ProductEntityTest {

  private ProductEntity createProduct(int stock) {
    return new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), stock);
  }

  @Test
  @DisplayName("decreaseStock - should decrease stock when quantity is valid")
  void decreaseStock_withValidQuantity_decreasesStock() {
    var product = createProduct(100);

    product.decreaseStock(5);

    assertEquals(95, product.getStock());
  }

  @Test
  @DisplayName("decreaseStock - should throw IllegalArgumentException when quantity is zero")
  void decreaseStock_withZeroQuantity_throwsException() {
    var product = createProduct(100);

    assertThrows(IllegalArgumentException.class, () -> product.decreaseStock(0));
  }

  @Test
  @DisplayName("decreaseStock - should throw IllegalArgumentException when quantity is negative")
  void decreaseStock_withNegativeQuantity_throwsException() {
    var product = createProduct(100);

    assertThrows(IllegalArgumentException.class, () -> product.decreaseStock(-1));
  }

  @Test
  @DisplayName("decreaseStock - should throw IllegalArgumentException when quantity is negative")
  void decreaseStock_withInsufficientStock_throwsException() {
    var product = createProduct(5);

    assertThrows(InsufficientResourcesException.class, () -> product.decreaseStock(10));
  }

  @Test
  @DisplayName("restock - should increase stock when quantity is valid")
  void restock_withValidQuantity_increasesStock() {
    var product = createProduct(100);

    product.restock(50);

    assertEquals(150, product.getStock());
  }

  @Test
  @DisplayName("restock - should throw IllegalArgumentException when quantity is zero")
  void restock_withZeroQuantity_throwsException() {
    var product = createProduct(100);

    assertThrows(IllegalArgumentException.class, () -> product.restock(0));
  }

  @Test
  @DisplayName("restock - should throw IllegalArgumentException when quantity is negative")
  void restock_withNegativeQuantity_throwsException() {
    var product = createProduct(100);

    assertThrows(IllegalArgumentException.class, () -> product.restock(-1));
  }

  @Test
  @DisplayName("increaseStock - should increase stock when quantity is valid")
  void increaseStock_withValidQuantity_increasesStock() {
    var product = createProduct(100);
    product.decreaseStock(10);
    assertEquals(90, product.getStock());

    product.increaseStock(10);

    assertEquals(100, product.getStock());
  }

  @Test
  @DisplayName("increaseStock - should throw IllegalArgumentException when quantity is zero")
  void increaseStock_withZeroQuantity_throwsException() {
    var product = createProduct(100);

    assertThrows(IllegalArgumentException.class, () -> product.increaseStock(0));
  }

  @Test
  @DisplayName("increaseStock - should throw IllegalArgumentException when quantity is negative")
  void increaseStock_withNegativeQuantity_throwsException() {
    var product = createProduct(100);

    assertThrows(IllegalArgumentException.class, () -> product.increaseStock(-1));
  }

  @Test
  @DisplayName("softDelete/restore - should toggle the deleted flag")
  void softDeleteAndRestore_togglesDeletedFlag() {
    var product = createProduct(100);
    assertFalse(product.isDeleted());

    product.softDelete();
    assertTrue(product.isDeleted());

    product.restore();
    assertFalse(product.isDeleted());
  }
}