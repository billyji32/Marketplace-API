package com.intuit.cg.backendtechassessment.repository;

import com.intuit.cg.backendtechassessment.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
