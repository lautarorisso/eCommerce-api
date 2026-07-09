package com.lautarorisso.eCommerce_api.service;

import com.lautarorisso.eCommerce_api.dto.request.LoginRequest;
import com.lautarorisso.eCommerce_api.dto.request.RegisterRequest;
import com.lautarorisso.eCommerce_api.dto.response.AuthResponse;

public interface AuthenticationService {
  AuthResponse login(LoginRequest request);

  AuthResponse register(RegisterRequest request);
}
