package com.lautarorisso.eCommerce_api.mapper;

import org.mapstruct.Mapper;

import com.lautarorisso.eCommerce_api.dto.response.productDto;
import com.lautarorisso.eCommerce_api.model.ProductEntity;

@Mapper(componentModel = "spring")
public interface productMapper {
  productDto toDto(ProductEntity product);

  ProductEntity toEntity(productDto dto);
}
