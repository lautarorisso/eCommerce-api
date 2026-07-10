package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.ProductDto;
import com.lautarorisso.eCommerce_api.model.ProductEntity;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {
  @Mapping(target = "categoryId", expression = "java(product.getCategory() != null ? product.getCategory().getId() : null)")
  @Mapping(target = "categoryName", expression = "java(product.getCategory() != null ? product.getCategory().getName() : null)")
  ProductDto toDto(ProductEntity product);

  @Mapping(target = "category", ignore = true)
  ProductEntity toEntity(CreateProductRequest request);
}
