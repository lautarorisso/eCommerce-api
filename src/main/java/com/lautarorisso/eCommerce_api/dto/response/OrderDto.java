package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.lautarorisso.eCommerce_api.enums.OrderStatus;

public record OrderDto(Long id, List<OrderItemDto> items, BigDecimal totalPrice, OrderStatus status) {
}
