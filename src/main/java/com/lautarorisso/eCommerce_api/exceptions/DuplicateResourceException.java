package com.lautarorisso.eCommerce_api.exceptions;

public class DuplicateResourceException extends RuntimeException {

  public DuplicateResourceException(String message) {
    super(message);
  }

  public DuplicateResourceException(String resource, String field, String value) {
    super("%s with %s '%s' already exists".formatted(resource, field, value));
  }
}
