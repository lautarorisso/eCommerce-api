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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

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
  @DisplayName("getAllUsers - should return paginated users")
  void getAllUsers_withFilters_returnsPage() {
    var pageable = PageRequest.of(0, 20);
    var page = new PageImpl<>(java.util.List.of(user));

    when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    when(userMapper.toDto(user)).thenReturn(userDto);

    Page<UserDto> result = userService.getAllUsers(null, null, pageable);

    assertEquals(1, result.getTotalElements());
    verify(userRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  @DisplayName("getUserById - should return UserDto when user exists")
  void getUserById_whenExists_returnsDto() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);

    var result = userService.getUserById(1L);

    assertEquals("johndoe", result.username());
    verify(userRepository).findById(1L);
  }

  @Test
  @DisplayName("getUserById - should throw ResourceNotFoundException when user does not exist")
  void getUserById_whenNotExists_throwsException() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
  }

  @Test
  @DisplayName("getUserByEmail - should return UserDto when user exists")
  void getUserByEmail_whenExists_returnsDto() {
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);

    var result = userService.getUserByEmail("user@example.com");

    assertEquals("user@example.com", result.email());
    verify(userRepository).findByEmail("user@example.com");
  }

  @Test
  @DisplayName("createUser - should create and return UserDto")
  void createUser_withValidData_returnsDto() {
    var request = new CreateUserRequest("johndoe", "user@example.com", "password123");

    when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded");
    when(userRepository.save(any())).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);

    var result = userService.createUser(request);

    assertEquals("johndoe", result.username());
    verify(userRepository).existsByEmail("user@example.com");
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("createUser - should throw DuplicateResourceException when email already exists")
  void createUser_withDuplicateEmail_throwsException() {
    var request = new CreateUserRequest("johndoe", "existing@example.com", "password123");

    when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateUser - should update and return UserDto")
  void updateUser_withValidData_returnsDto() {
    var request = new UpdateUserRequest("newusername", "new@example.com");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(new UserDto(1L, "newusername", "new@example.com", Role.USER));

    var result = userService.updateUser(1L, request);

    assertEquals("newusername", result.username());
    verify(userRepository).findById(1L);
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("deleteUser - should delete when user exists and has no references")
  void deleteUser_whenValid_deletesSuccessfully() {
    when(userRepository.existsById(1L)).thenReturn(true);
    when(cartRepository.existsByUserId(1L)).thenReturn(false);
    when(orderRepository.existsByUserId(1L)).thenReturn(false);

    userService.deleteUser(1L);

    verify(userRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteUser - should throw InvalidOperationException when user has cart")
  void deleteUser_whenHasCart_throwsException() {
    when(userRepository.existsById(1L)).thenReturn(true);
    when(cartRepository.existsByUserId(1L)).thenReturn(true);

    assertThrows(InvalidOperationException.class, () -> userService.deleteUser(1L));
    verify(userRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("deleteUser - should throw ResourceNotFoundException when user does not exist")
  void deleteUser_whenNotExists_throwsException() {
    when(userRepository.existsById(99L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
  }
}
