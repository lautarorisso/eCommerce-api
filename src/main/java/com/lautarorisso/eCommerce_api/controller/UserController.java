package com.lautarorisso.eCommerce_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lautarorisso.eCommerce_api.dto.request.CreateUserRequest;
import com.lautarorisso.eCommerce_api.dto.request.UpdateUserRequest;
import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.enums.Role;
import com.lautarorisso.eCommerce_api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Manage users (admin only)")
public class UserController {

  private final UserService userService;

  @GetMapping
  @Operation(summary = "Get all users", description = "Returns a paginated list of users with optional filters (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Users found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN role")
  })
  public Page<UserDto> getAllUsers(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Role role,
      @PageableDefault(size = 20) Pageable pageable) {
    return userService.getAllUsers(search, role, pageable);
  }

  @GetMapping("/{userId}")
  @Operation(summary = "Get user by ID", description = "Returns a single user (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN role"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public UserDto getUserById(@PathVariable Long userId) {
    return userService.getUserById(userId);
  }

  @GetMapping("/me")
  @Operation(summary = "Get current user", description = "Returns the authenticated user's profile")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public UserDto getCurrentUser() {
    return userService.getCurrentUser();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a user", description = "Creates a new user (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User created"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN role"),
      @ApiResponse(responseCode = "409", description = "Email already in use")
  })
  public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
    return userService.createUser(request);
  }

  @PatchMapping("/{userId}")
  @Operation(summary = "Update a user", description = "Updates some fields of a user by ID (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User updated"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN role"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "409", description = "Email already in use")
  })
  public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
    return userService.updateUser(userId, request);
  }

  @DeleteMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a user", description = "Deletes a user by ID (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "User deleted"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN role"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public void deleteUser(@PathVariable Long userId) {
    userService.deleteUser(userId);
  }
}
