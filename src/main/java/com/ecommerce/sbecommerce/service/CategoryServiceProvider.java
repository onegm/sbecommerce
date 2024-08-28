package com.ecommerce.sbecommerce.service;

import com.ecommerce.sbecommerce.payload.CategoryDTO;
import com.ecommerce.sbecommerce.payload.CategoryResponse;

public interface CategoryServiceProvider {
    CategoryResponse get(Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending);
    CategoryDTO get(Long categoryId);
    CategoryDTO create(CategoryDTO categoryDTO);
    CategoryDTO delete(Long categoryId);
    CategoryDTO update(CategoryDTO categoryDTO);
}
