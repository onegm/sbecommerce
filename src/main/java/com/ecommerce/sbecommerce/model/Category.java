package com.ecommerce.sbecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 3, message = "Name must contain at least 3 characters.")
    private String name;

//    Getters & Setters not needed after lombok @Data annotation
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;

    @PreRemove
    public void preRemove(){
        products.stream().forEach(product -> product.setCategory(null));
    }
}
