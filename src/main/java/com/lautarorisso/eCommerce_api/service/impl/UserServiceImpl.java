package com.lautarorisso.eCommerce_api.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateUserRequest;
import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.exceptions.DuplicateResourceException;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.UserMapper;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.OrderRepository;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.service.UserService;
import com.lautarorisso.eCommerce_api.specification.UserSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Override
  public UserDto createUser(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new DuplicateResourceException("User", "email", request.email());
    }
    String hashedPassword = passwordEncoder.encode(request.password());
    UserEntity user = new UserEntity(request.username(), request.email(), hashedPassword, Role.USER);
    UserEntity savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
  }

  @Override
  public Page<UserDto> getAllUsers(String search, Role role, Pageable pageable) {
    Specification<UserEntity> spec = Specification
        .where(UserSpecification.usernameOrEmailContains(search))
        .and(UserSpecification.roleEquals(role));
    return userRepository.findAll(spec, pageable).map(userMapper::toDto);
  }

  @Override
  public UserDto getUserById(Long userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    return userMapper.toDto(user);
  }

  @Override
  public UserDto updateUser(Long userId, UpdateUserRequest request) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    if (request.username() != null) {
      user.changeUsername(request.username());
    }
    if (request.email() != null) {
      if (!request.email().equals(user.getEmail()) && userRepository.existsByEmail(request.email())) {
        throw new DuplicateResourceException("User", "email", request.email());
      }
      user.changeEmail(request.email());
    }
    UserEntity updatedUser = userRepository.save(user);
    return userMapper.toDto(updatedUser);
  }

  @Override
  public void deleteUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User", userId);
    }
    if (cartRepository.existsByUserId(userId)) {
      throw new InvalidOperationException("Cannot delete user: user has an active cart");
    }
    if (orderRepository.existsByUserId(userId)) {
      throw new InvalidOperationException("Cannot delete user: user has orders");
    }
    userRepository.deleteById(userId);
  }

  @Override
  public UserDto getUserByEmail(String email) {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    return userMapper.toDto(user);
  }

}
