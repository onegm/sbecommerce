package com.ecommerce.sbecommerce.repository;

import com.ecommerce.sbecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name); // Automatically implemented by JPA based on method name structure.

    List<Category> findAllByName(String name);
}
