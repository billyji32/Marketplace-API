package com.intuit.cg.backendtechassessment.users.repository;

import com.intuit.cg.backendtechassessment.users.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
