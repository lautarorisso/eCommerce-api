package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lautarorisso.eCommerce_api.dto.response.orderDto;
import com.lautarorisso.eCommerce_api.model.OrderEntity;

@Mapper(componentModel = "spring", uses = orderItemMapper.class)
public interface orderMapper {
  orderDto toDto(OrderEntity order);

  @Mapping(target = "user", ignore = true)
  OrderEntity toEntity(orderDto dto);
}
