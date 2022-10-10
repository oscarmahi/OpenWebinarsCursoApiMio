package com.openwebinars.rest.dto.converter;

import com.openwebinars.rest.dto.ProductDTO;
import com.openwebinars.rest.model.Product;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDTOConverter {

    private final ModelMapper modelMapper;

    public ProductDTO convertToDto(Product product){
        return modelMapper.map(product, ProductDTO.class);
    }
}
