package com.intuit.cg.backendtechassessment.users.repository;

import com.intuit.cg.backendtechassessment.users.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
