package com.lautarorisso.eCommerce_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category information")
public record CategoryDto(
    @Schema(description = "Category ID", example = "1")
    Long id,

    @Schema(description = "Category name", example = "Electronics")
    String name) {
}
