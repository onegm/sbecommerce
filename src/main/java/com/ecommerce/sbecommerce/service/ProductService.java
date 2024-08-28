package com.ecommerce.sbecommerce.service;

import com.ecommerce.sbecommerce.exceptions.APIException;
import com.ecommerce.sbecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecommerce.model.Category;
import com.ecommerce.sbecommerce.model.Product;
import com.ecommerce.sbecommerce.payload.ProductDTO;
import com.ecommerce.sbecommerce.payload.ProductResponse;
import com.ecommerce.sbecommerce.repository.CartItemRepository;
import com.ecommerce.sbecommerce.repository.ProductRepository;
import com.ecommerce.sbecommerce.util.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductService implements ProductServiceProvider{
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ModelMapper modelMapper;
    @Value("${project.image}")
    private String imagesPath;
    @Autowired
    CartItemService cartItemService;

    @Override
    public ProductResponse get(Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending) {
        Sort sortByAndOrder = sortAscending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        if(productPage.getContent().isEmpty())
            throw new APIException("No products have been created.");

        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending) {
        categoryService.get(categoryId);

        Sort sortByAndOrder = sortAscending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageDetails);
        if(productPage.getContent().isEmpty())
            throw new APIException("No products have been created in this category.");

        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, boolean sortAscending) {
        Sort sortByAndOrder = sortAscending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByNameLikeIgnoreCase("%" + keyword + "%", pageDetails);
        if(productPage.getContent().isEmpty())
            throw new APIException("No products associated with keyword: " + keyword);

        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO create(ProductDTO productDTO, Long categoryId) {
        Category category = modelMapper.map(categoryService.get(categoryId), Category.class);
        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);

        if(product.getDiscount() != 0.0){
            product.setSpecialPrice(product.getPrice()*(1.0 - product.getDiscount()/100));
        } else{
            product.setSpecialPrice(product.getPrice());
        }

        product = productRepository.save(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));
        cartItemService.handleProductDelete(product);
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Transactional
    @Override
    public ProductDTO update(ProductDTO productDTO) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        Product originalProduct = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productDTO.getId()));
        modelMapper.map(productDTO, originalProduct);
        Product updatedProduct = productRepository.save(originalProduct);

        cartItemService.handleProductUpdate(updatedProduct);

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateImage(Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

        String fileName = FileService.uploadImage(imagesPath, image);
        product.setImage(fileName);
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));
    }

    @Override
    public void reduceStock(Product product, Integer quantity) {
        Product dbProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", product.getId()));

        int newQuantity = dbProduct.getQuantity() - quantity;
        if(newQuantity < 0)
            throw new APIException("Product " + product.getName() + "not in stock for requested quantity: " + quantity);
        dbProduct.setQuantity(newQuantity);
    }
}
