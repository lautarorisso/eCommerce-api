package com.lautarorisso.eCommerce_api.exceptions;

public class InsufficientResourcesException extends RuntimeException {

  public InsufficientResourcesException(String productName) {
    super("Insufficient stock for product '%s'".formatted(productName));
  }
}
