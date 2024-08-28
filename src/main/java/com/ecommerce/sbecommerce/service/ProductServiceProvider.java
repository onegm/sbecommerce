package com.ecommerce.sbecommerce.service;

import com.ecommerce.sbecommerce.model.Product;
import com.ecommerce.sbecommerce.payload.ProductDTO;
import com.ecommerce.sbecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductServiceProvider {
    ProductResponse get(Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending);
    ProductResponse getByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending);
    ProductResponse getByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending);
    ProductDTO create(ProductDTO productDTO, Long categoryId);
    ProductDTO delete(Long productId);
    ProductDTO update(ProductDTO productDTO);

    ProductDTO updateImage(Long productId, MultipartFile image);

    Product findById(Long productId);

    void reduceStock(Product product, Integer quantity);
}
