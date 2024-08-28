package com.ecommerce.sbecommerce.repository;

import com.ecommerce.sbecommerce.model.Role;
import com.ecommerce.sbecommerce.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}