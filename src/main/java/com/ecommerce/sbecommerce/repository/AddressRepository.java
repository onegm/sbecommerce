package com.ecommerce.sbecommerce.repository;

import com.ecommerce.sbecommerce.model.Address;
import com.ecommerce.sbecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
