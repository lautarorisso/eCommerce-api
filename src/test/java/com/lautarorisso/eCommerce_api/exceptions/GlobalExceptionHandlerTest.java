package com.lautarorisso.eCommerce_api.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  @DisplayName("handleIllegalState - should map IllegalStateException to 409 Conflict")
  void handleIllegalState_returns409() {
    var response = handler.handleIllegalState(new IllegalStateException("Order cannot be shipped"));

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.CONFLICT.value(), response.getBody().status());
  }
}
