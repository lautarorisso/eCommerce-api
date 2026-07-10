package com.lautarorisso.eCommerce_api.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.request.LoginRequest;
import com.lautarorisso.eCommerce_api.dto.request.RegisterRequest;
import com.lautarorisso.eCommerce_api.dto.response.AuthResponse;
import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.exceptions.DuplicateResourceException;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.security.JwtService;
import com.lautarorisso.eCommerce_api.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final PasswordEncoder passwordEncoder;

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
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new DuplicateResourceException("User", "email", request.email());
    }
    String hashedPassword = passwordEncoder.encode(request.password());
    UserEntity user = new UserEntity(request.username(), request.email(), hashedPassword, Role.USER);
    userRepository.save(user);
    cartRepository.save(new CartEntity(user));
    return buildAuthResponse(user);
  }

  private AuthResponse buildAuthResponse(UserEntity user) {
    String token = jwtService.createToken(user.getId(), user.getEmail(), user.getRole().name());
    return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name());
  }
}
