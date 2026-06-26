package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.model.UserEntity;

public interface UserService {
  UserDto createUser(CreateUserRequest request);

  List<UserDto> getAllUsers();

  UserDto getUserById(Long userId);

  UserDto updateUser(Long userId, UpdateUserRequest request);

  void deleteUser(Long userId);

  UserDto gerUserByEmail(String email);

  boolean existsByEmail(String email);
}
