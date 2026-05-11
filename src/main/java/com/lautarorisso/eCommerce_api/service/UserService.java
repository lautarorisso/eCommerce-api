package com.lautarorisso.eCommerce_api.service;

import java.util.List;

import com.lautarorisso.eCommerce_api.model.UserEntity;

public interface UserService {
    UserEntity createUser(UserEntity user);

    List<UserEntity> getAllUsers();

    UserEntity getUserById(Long id);

    UserEntity updateUser(Long id, UserEntity user);

    void deleteUser(Long id);
}