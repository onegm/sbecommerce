package com.ecommerce.sbecommerce.service;

import com.ecommerce.sbecommerce.config.AppConstants;
import com.ecommerce.sbecommerce.exceptions.APIException;
import com.ecommerce.sbecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecommerce.model.Category;
import com.ecommerce.sbecommerce.payload.CategoryDTO;
import com.ecommerce.sbecommerce.payload.CategoryResponse;
import com.ecommerce.sbecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements CategoryServiceProvider{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse get(Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending) {
        Sort sortByAndOrder = sortAscending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty())
            throw new APIException("No categories have been created.");

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO get(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category duplicateCategory = categoryRepository.findByName(category.getName());
        if (duplicateCategory != null)
            throw new APIException("Category with name \"" + category.getName() + "\" already exists.");
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO delete(Long categoryId) {
        Category categoryToDelete = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        categoryRepository.delete(categoryToDelete);
        return modelMapper.map(categoryToDelete, CategoryDTO.class);

//        categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
//        Category toDelete = categories.stream()
//                .filter(c-> c.getId().equals(categoryId))
//                .findFirst()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
//
//        categories.remove(toDelete);
    }

    @Override
    public CategoryDTO update(CategoryDTO categoryDTO){
        Category categoryToUpdate = modelMapper.map(categoryDTO, Category.class);

        categoryRepository.findById(categoryToUpdate.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryToUpdate.getId()));

        categoryRepository.findAllByName(categoryToUpdate.getName()).forEach(c -> {
            if(!c.getId().equals(categoryToUpdate.getId()))
                throw new APIException("Category with name \"" + categoryToUpdate.getName() + "\" already exists.");
        });

        Category updatedCategory = categoryRepository.save(categoryToUpdate);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }
}
