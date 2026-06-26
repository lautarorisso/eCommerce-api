package com.lautarorisso.eCommerce_api.dto.response;

import java.util.List;

public record cartDto(Long id, List<cartItemDto> items) {
}
