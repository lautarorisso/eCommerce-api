package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;

import com.lautarorisso.eCommerce_api.dto.request.CreateCategoryRequest;
import com.lautarorisso.eCommerce_api.dto.response.CategoryDto;
import com.lautarorisso.eCommerce_api.model.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryDto toDto(CategoryEntity entity);

  CategoryEntity toEntity(CreateCategoryRequest request);
}
