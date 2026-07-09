package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;

import com.lautarorisso.eCommerce_api.dto.request.CreateProductRequest;
import com.lautarorisso.eCommerce_api.dto.response.ProductDto;
import com.lautarorisso.eCommerce_api.model.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  ProductDto toDto(ProductEntity product);

  ProductEntity toEntity(CreateProductRequest request);
}
