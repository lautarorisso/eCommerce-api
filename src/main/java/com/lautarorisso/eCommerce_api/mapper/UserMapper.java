package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lautarorisso.eCommerce_api.dto.response.UserDto;
import com.lautarorisso.eCommerce_api.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(UserEntity user);

  @Mapping(target = "password", ignore = true)
  UserEntity toEntity(UserDto dto);
}
