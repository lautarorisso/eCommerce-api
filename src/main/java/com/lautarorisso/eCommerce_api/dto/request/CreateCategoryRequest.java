package com.lautarorisso.eCommerce_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a new category")
public record CreateCategoryRequest(
    @Schema(description = "Category name", example = "Electronics")
    @NotBlank @Size(max = 100) String name) {
}
