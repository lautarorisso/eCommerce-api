package com.lautarorisso.eCommerce_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to update an existing category")
public record UpdateCategoryRequest(
    @Schema(description = "Category name", example = "Electronics")
    @NotBlank String name) {
}
