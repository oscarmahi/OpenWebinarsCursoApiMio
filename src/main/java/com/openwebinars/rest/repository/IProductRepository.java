package com.openwebinars.rest.repository;

import com.openwebinars.rest.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductRepository extends JpaRepository<Product, Long> {

}
