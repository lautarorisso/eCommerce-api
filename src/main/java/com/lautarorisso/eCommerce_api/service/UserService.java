package com.lautarorisso.eCommerce_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateUserRequest;
import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.enums.Role;

public interface UserService {
  UserDto createUser(CreateUserRequest request);

  Page<UserDto> getAllUsers(String search, Role role, Pageable pageable);

  UserDto getUserById(Long userId);

  UserDto updateUser(Long userId, UpdateUserRequest request);

  void deleteUser(Long userId);

  UserDto getCurrentUser();
}
