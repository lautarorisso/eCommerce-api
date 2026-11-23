package com.lautarorisso.eCommerce_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.exceptions.DuplicateResourceException;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.UserMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.OrderRepository;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  private final UserEntity user = new UserEntity("johndoe", "user@example.com", "encoded", Role.USER);
  private final UserDto userDto = new UserDto(1L, "johndoe", "user@example.com", Role.USER);

  @Test
  @DisplayName("createUser - should create and return UserDto")
  void createUser_withValidData_returnsDto() {
    var request = new CreateUserRequest("johndoe", "user@example.com", "password123", Role.USER);

    when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded");
    when(userRepository.save(any())).thenReturn(user);
    when(cartRepository.save(any())).thenReturn(null);
    when(userMapper.toDto(user)).thenReturn(userDto);

    var result = userService.createUser(request);

    assertEquals("johndoe", result.username());
    verify(userRepository).existsByEmail("user@example.com");
    verify(userRepository).save(any());
    verify(cartRepository).save(any());
  }

  @Test
  @DisplayName("deleteUser - should delete when user exists and has no references")
  void deleteUser_whenValid_deletesSuccessfully() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
    when(orderRepository.existsByUserId(1L)).thenReturn(false);

    userService.deleteUser(1L);

    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("deleteUser - should throw InvalidOperationException when user has active cart")
  void deleteUser_whenHasCart_throwsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(new CartEntity(user)));

    assertThrows(InvalidOperationException.class, () -> userService.deleteUser(1L));
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("getUserById - should return user when found")
  void getUserById_whenFound_returnsDto() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);

    var result = userService.getUserById(1L);

    assertEquals("johndoe", result.username());
    verify(userRepository).findById(1L);
  }

  @Test
  @DisplayName("updateUser - should update user fields")
  void updateUser_withValidData_updatesSuccessfully() {
    var updateRequest = new com.lautarorisso.eCommerce_api.dto.request.UpdateUserRequest("janedoe", null);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(new UserDto(1L, "janedoe", "user@example.com", Role.USER));

    var result = userService.updateUser(1L, updateRequest);

    assertEquals("janedoe", result.username());
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("restoreUser - should restore a soft-deleted user")
  void restoreUser_whenDeleted_restoresAndReturnsDto() {
    var deleted = new UserEntity("janedoe", "jane@example.com", "encoded", Role.USER);
    deleted.softDelete();
    when(userRepository.findDeletedById(1L)).thenReturn(Optional.of(deleted));
    when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
    when(userRepository.save(any())).thenReturn(deleted);
    when(userMapper.toDto(deleted)).thenReturn(new UserDto(1L, "janedoe", "jane@example.com", Role.USER));

    var result = userService.restoreUser(1L);

    assertFalse(deleted.isDeleted());
    assertEquals("janedoe", result.username());
    verify(userRepository).save(deleted);
  }

  @Test
  @DisplayName("restoreUser - should throw when no soft-deleted user exists")
  void restoreUser_whenNotFound_throws() {
    when(userRepository.findDeletedById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.restoreUser(1L));
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("restoreUser - should throw when an active user already has the email")
  void restoreUser_whenEmailTaken_throws() {
    var deleted = new UserEntity("johndoe", "user@example.com", "encoded", Role.USER);
    deleted.softDelete();
    when(userRepository.findDeletedById(1L)).thenReturn(Optional.of(deleted));
    when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> userService.restoreUser(1L));
    verify(userRepository, never()).save(any());
  }
}
