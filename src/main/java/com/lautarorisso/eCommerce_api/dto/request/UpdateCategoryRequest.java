package com.lautarorisso.eCommerce_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(@NotBlank String name) {
}
