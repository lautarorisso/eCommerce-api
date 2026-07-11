package com.lautarorisso.eCommerce_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ECommerceApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private String login(String email, String password) throws Exception {
    var result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "email": "%s", "password": "%s" }
                """.formatted(email, password)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty())
        .andReturn();

    return result.getResponse().getContentAsString().split("\"token\":\"")[1].split("\"")[0];
  }

  private String registerAndLogin(String username, String email, String password) throws Exception {
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "username": "%s", "email": "%s", "password": "%s" }
                """.formatted(username, email, password)))
        .andExpect(status().isCreated());

    return login(email, password);
  }

  private long extractJsonLong(String json, String key) {
    String search = "\"" + key + "\":";
    int start = json.indexOf(search) + search.length();
    int end = start;
    while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
      end++;
    }
    return Long.parseLong(json.substring(start, end).trim());
  }

  @Test
  @DisplayName("Full flow: register -> login -> add to cart -> create order")
  void fullFlow_registerToAddToCartToCreateOrder() throws Exception {
    String adminToken = login("admin@email.com", "admin123");

    mockMvc.perform(post("/api/products")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "name": "TestMouse", "description": "A test mouse",
                  "unitPrice": 29.99, "stock": 100, "categoryId": 1 }
                """))
        .andExpect(status().isCreated());

    String userToken = registerAndLogin("testuser", "test@example.com", "password123");

    var meResult = mockMvc.perform(get("/api/users/me")
            .header("Authorization", "Bearer " + userToken)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    long userId = extractJsonLong(meResult.getResponse().getContentAsString(), "id");

    var cartResult = mockMvc.perform(get("/api/carts/user/" + userId)
            .header("Authorization", "Bearer " + userToken)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    long cartId = extractJsonLong(cartResult.getResponse().getContentAsString(), "id");

    mockMvc.perform(post("/api/carts/" + cartId + "/items")
            .header("Authorization", "Bearer " + userToken)
            .param("productId", "1")
            .param("quantity", "1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.items").isArray());

    mockMvc.perform(post("/api/orders")
            .header("Authorization", "Bearer " + userToken)
            .param("cartId", String.valueOf(cartId))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }
}
