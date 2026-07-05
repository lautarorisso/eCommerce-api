package com.lautarorisso.eCommerce_api.exceptions;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String resource, Long id) {
    super("%s with id %d not found".formatted(resource, id));
  }

  public ResourceNotFoundException(String resource, String field, String value) {
    super("%s with %s '%s' not found".formatted(resource, field, value));
  }
}
