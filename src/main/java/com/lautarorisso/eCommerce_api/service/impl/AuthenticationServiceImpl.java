package com.lautarorisso.eCommerce_api.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.LoginRequest;
import com.lautarorisso.eCommerce_api.dto.response.AuthResponse;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.security.JwtService;
import com.lautarorisso.eCommerce_api.service.AuthenticationService;
import com.lautarorisso.eCommerce_api.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final UserService userService;

  @Transactional(readOnly = true)
  @Override
  public AuthResponse login(LoginRequest request) {
    var auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    var user = userRepository.findByEmail(auth.getName())
        .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
    return buildAuthResponse(user);
  }

  @Transactional
  @Override
  public AuthResponse register(CreateUserRequest request) {
    userService.createUser(request);
    UserEntity user = userRepository.findByEmail(request.email()).orElseThrow();
    return buildAuthResponse(user);
  }

  private AuthResponse buildAuthResponse(UserEntity user) {
    String token = jwtService.createToken(user.getId(), user.getEmail(), user.getRole().name());
    return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name());
  }
}
