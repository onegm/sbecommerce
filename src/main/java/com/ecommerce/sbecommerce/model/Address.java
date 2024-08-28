package com.ecommerce.sbecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String buildingNumber;

    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters long.")
    private String street;

    @NotBlank
    @Size(min = 3, message = "City name must be at least 3 characters long.")
    private String city;

    @NotBlank
    @Size(min = 5, message = "Country name must be at least 5 characters long.")
    private String country;

    @ManyToMany(mappedBy = "addresses")
    @JsonIgnore
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "address")
    @JsonIgnore
    private List<Order> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(buildingNumber, address.buildingNumber) && Objects.equals(street, address.street) && Objects.equals(city, address.city) && Objects.equals(country, address.country);
    }

    public void addUser(User user) {
        if(users.contains(user))
            return;
        users.add(user);
        user.addAddress(this);
    }

    public void removeUsers(){
        getUsers().forEach(user -> user.removeAddress(this));
        setUsers(null);
    }
}
