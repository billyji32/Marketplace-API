package com.intuit.cg.backendtechassessment.repository;

import com.intuit.cg.backendtechassessment.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
