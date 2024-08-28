package com.ecommerce.sbecommerce.repository;

import com.ecommerce.sbecommerce.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
