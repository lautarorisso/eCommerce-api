package com.lautarorisso.eCommerce_api.exceptions;

public class InsufficientResourcesException extends RuntimeException {

  public InsufficientResourcesException(String productName, int requested, int available) {
    super(
        "Insufficient stock for product '%s': requested %d, available %d".formatted(productName, requested, available));
  }
}
