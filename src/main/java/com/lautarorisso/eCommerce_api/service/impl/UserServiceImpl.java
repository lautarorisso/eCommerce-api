package com.lautarorisso.eCommerce_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateUserRequest;
import com.lautarorisso.eCommerce_api.dto.response.userDto;
import com.lautarorisso.eCommerce_api.mapper.userMapper;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final userMapper userMapper;

  @Override
  public userDto createUser(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new RuntimeException("Email already exists");
    }
    UserEntity user = new UserEntity(request.username(), request.email(), request.password());
    UserEntity savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
  }

  @Override
  public List<userDto> getAllUsers() {
    return userRepository.findAll().stream().map(userMapper::toDto).toList();
  }

  @Override
  public userDto getUserById(Long userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return userMapper.toDto(user);
  }

  @Override
  public userDto updateUser(Long userId, UpdateUserRequest request) {
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
    return userMapper.toDto(updatedUser);
  }

  @Override
  public void deleteUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new RuntimeException("User not found");
    }
    userRepository.deleteById(userId);
  }

  @Override
  public userDto getUserByEmail(String email) {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return userMapper.toDto(user);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
