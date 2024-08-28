package com.ecommerce.sbecommerce.controller;

import com.ecommerce.sbecommerce.config.AppConstants;
import com.ecommerce.sbecommerce.payload.CategoryDTO;
import com.ecommerce.sbecommerce.payload.CategoryResponse;
import com.ecommerce.sbecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // prepends all mappings in this class with "/api" without needing to manually type
public class CategoryController {
    @Autowired
    CategoryService categoryService;

//    @RequestMapping(value = "api/public/categories", method = RequestMethod.GET)
    @GetMapping("public/categories")
    public ResponseEntity<CategoryResponse> get(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(name = "sortAscending", defaultValue = AppConstants.SORT_ASCENDING) boolean sortAscending) {
        CategoryResponse categoryResponse = categoryService.get(pageNumber, pageSize, sortBy, sortAscending);
        return ResponseEntity.ok(categoryResponse);
    }

    @PostMapping("admin/category")
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.create(categoryDTO);
        return new ResponseEntity(savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("admin/category/{categoryId}")
    public ResponseEntity<CategoryDTO> delete(@PathVariable Long categoryId){
//        try{
        CategoryDTO deletedCategory = categoryService.delete(categoryId);
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
//            return ResponseEntity.ok(status);
//            return ResponseEntity.status(HttpStatus.OK).body(status);
//        }
//        catch (ResponseStatusException e){
//            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
//        }
    }

    @PutMapping("admin/category")
    public ResponseEntity<CategoryDTO> update(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO updatedCategory = categoryService.update(categoryDTO);
        return new ResponseEntity<>(updatedCategory, HttpStatus.ACCEPTED);
    }
}
