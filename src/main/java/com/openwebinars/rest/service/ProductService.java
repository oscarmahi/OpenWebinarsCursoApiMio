package com.openwebinars.rest.service;

import com.openwebinars.rest.dto.CreateProductDTO;
import com.openwebinars.rest.model.Category;
import com.openwebinars.rest.repository.IProductRepository;
import com.openwebinars.rest.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    IProductRepository iProductRepository;

    @Autowired
    CategoryService categoriaService;

    public List<Product> findAllService() {
        return iProductRepository.findAll();
    }

    public Optional<Product> findByIdService(Long id) {
        return iProductRepository.findById(id);
    }

    public Product saveService(Product newP) {
        return iProductRepository.save(newP);
    }

    public boolean existsByIdService(Long id) {
        return iProductRepository.existsById(id);
    }

    public void deleteByIdService(Long id) {
        iProductRepository.deleteById(id);
    }

    public Product saveServiceConDTO(CreateProductDTO newProductDTO) {

        Product newProduct = new Product();
        newProduct.setName(newProductDTO.getName());
        newProduct.setPrice(newProductDTO.getPrice());
        Category category = categoriaService.findByIdService(newProductDTO.getCategoryId()).orElse(null);
        newProduct.setCategory(category);

        return iProductRepository.save(newProduct);
    }

    public void deleteService(Product product) {
        iProductRepository.delete(product);
    }
}
