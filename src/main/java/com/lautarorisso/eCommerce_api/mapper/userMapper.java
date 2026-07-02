package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;

import com.lautarorisso.eCommerce_api.dto.response.userDto;
import com.lautarorisso.eCommerce_api.model.UserEntity;

@Mapper(componentModel = "spring")
public interface userMapper {
  userDto toDto(UserEntity user);

  UserEntity toEntity(userDto dto);
}
