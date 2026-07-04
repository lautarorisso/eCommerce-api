package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateUserRequest;
import com.lautarorisso.eCommerce_api.dto.response.userDto;

public interface UserService {
  userDto createUser(CreateUserRequest request);

  List<userDto> getAllUsers();

  userDto getUserById(Long userId);

  userDto updateUser(Long userId, UpdateUserRequest request);

  void deleteUser(Long userId);

  userDto getUserByEmail(String email);

  boolean existsByEmail(String email);
}
