package com.lautarorisso.eCommerce_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(@NotBlank String name) {
}
