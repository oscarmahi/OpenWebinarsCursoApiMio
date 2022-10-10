package com.openwebinars.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductDTO {

    private long id;
    private String name;
    private String image;
    private String categoryName;
}
