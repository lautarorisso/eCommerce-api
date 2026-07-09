package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lautarorisso.eCommerce_api.dto.response.OrderDto;
import com.lautarorisso.eCommerce_api.model.OrderEntity;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {
  OrderDto toDto(OrderEntity order);
}
