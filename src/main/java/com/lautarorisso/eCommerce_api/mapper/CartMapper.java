package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lautarorisso.eCommerce_api.dto.response.CartDto;
import com.lautarorisso.eCommerce_api.model.CartEntity;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {
  @Mapping(target = "total", expression = "java(cart.calculateTotal())")
  CartDto toDto(CartEntity cart);
}
