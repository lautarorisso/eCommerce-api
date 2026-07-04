package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lautarorisso.eCommerce_api.dto.response.CartItemDto;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
  @Mapping(source = "product.id", target = "productId")
  @Mapping(source = "product.name", target = "productName")
  CartItemDto toDto(CartItemEntity item);

  @Mapping(target = "cart", ignore = true)
  @Mapping(target = "product", ignore = true)
  CartItemEntity toEntity(CartItemDto dto);
}
