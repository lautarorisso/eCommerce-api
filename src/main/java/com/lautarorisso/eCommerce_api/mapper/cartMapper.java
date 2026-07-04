package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lautarorisso.eCommerce_api.dto.response.cartDto;
import com.lautarorisso.eCommerce_api.model.CartEntity;

@Mapper(componentModel = "spring", uses = cartItemMapper.class)
public interface cartMapper {
  @Mapping(target = "total", expression = "java(cart.calculateTotal())")
  cartDto toDto(CartEntity cart);

  @Mapping(target = "user", ignore = true)
  CartEntity toEntity(cartDto dto);
}
