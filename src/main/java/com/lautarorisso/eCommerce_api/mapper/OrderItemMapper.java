package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lautarorisso.eCommerce_api.dto.response.OrderItemDto;
import com.lautarorisso.eCommerce_api.model.OrderItemEntity;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
  @Mapping(source = "product.name", target = "productName")
  OrderItemDto toDto(OrderItemEntity item);
}
