package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateUserRequest;
import com.lautarorisso.eCommerce_api.dto.response.UserDto;

public interface UserService {
  UserDto createUser(CreateUserRequest request);

  List<UserDto> getAllUsers();

  UserDto getUserById(Long userId);

  UserDto updateUser(Long userId, UpdateUserRequest request);

  void deleteUser(Long userId);

  UserDto getUserByEmail(String email);

  boolean existsByEmail(String email);
}
