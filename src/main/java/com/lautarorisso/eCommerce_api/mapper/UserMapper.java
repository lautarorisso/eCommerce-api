package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;

import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(UserEntity user);

  UserEntity toEntity(UserDto dto);
}
