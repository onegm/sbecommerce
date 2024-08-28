package com.ecommerce.sbecommerce.controller;

import com.ecommerce.sbecommerce.config.AppConstants;
import com.ecommerce.sbecommerce.payload.ProductDTO;
import com.ecommerce.sbecommerce.payload.ProductResponse;
import com.ecommerce.sbecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("public/products")
    public ResponseEntity<ProductResponse> get(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(name = "sortAscending", defaultValue = AppConstants.SORT_ASCENDING) boolean sortAscending) {
        ProductResponse productResponse = productService.get(pageNumber, pageSize, sortBy, sortAscending);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(name = "sortAscending", defaultValue = AppConstants.SORT_ASCENDING) boolean sortAscending){

        ProductResponse productResponse = productService.getByCategory(categoryId, pageNumber, pageSize, sortBy, sortAscending);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(name = "sortAscending", defaultValue = AppConstants.SORT_ASCENDING) boolean sortAscending){

        ProductResponse productResponse = productService.getByKeyword(keyword, pageNumber, pageSize, sortBy, sortAscending);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO productDTO,
                                             @PathVariable Long categoryId){
        return new ResponseEntity(productService.create(productDTO, categoryId), HttpStatus.CREATED);
    }

    @PutMapping("admin/product")
    public ResponseEntity<ProductDTO> update(@Valid @RequestBody ProductDTO productDTO){
        return new ResponseEntity<>(productService.update(productDTO), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("admin/product/{productId}")
    public ResponseEntity<ProductDTO> delete(@PathVariable Long productId){
        return new ResponseEntity(productService.delete(productId), HttpStatus.OK);
    }

    @PutMapping("product/{productId}/image")
    public ResponseEntity<ProductDTO> updateImage(@PathVariable Long productId,
                                                  @RequestParam("image") MultipartFile image){
        return ResponseEntity.ok(productService.updateImage(productId, image));
    }

}
