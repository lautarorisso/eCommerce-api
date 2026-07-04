package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateUserRequest;
import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.mapper.UserMapper;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper UserMapper;

  @Override
  public UserDto createUser(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new RuntimeException("Email already exists");
    }
    UserEntity user = new UserEntity(request.username(), request.email(), request.password());
    UserEntity savedUser = userRepository.save(user);
    return UserMapper.toDto(savedUser);
  }

  @Override
  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream().map(UserMapper::toDto).toList();
  }

  @Override
  public UserDto getUserById(Long userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return UserMapper.toDto(user);
  }

  @Override
  public UserDto updateUser(Long userId, UpdateUserRequest request) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    if (request.username() != null) {
      user.changeUsername(request.username());
    }
    if (request.email() != null) {
      if (!request.email().equals(user.getEmail()) && userRepository.existsByEmail(request.email())) {
        throw new RuntimeException("Email already in use");
      }
      user.changeEmail(request.email());
    }
    UserEntity updatedUser = userRepository.save(user);
    return UserMapper.toDto(updatedUser);
  }

  @Override
  public void deleteUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new RuntimeException("User not found");
    }
    userRepository.deleteById(userId);
  }

  @Override
  public UserDto getUserByEmail(String email) {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return UserMapper.toDto(user);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
