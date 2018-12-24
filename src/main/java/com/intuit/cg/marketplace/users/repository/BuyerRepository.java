package com.intuit.cg.marketplace.users.repository;

import com.intuit.cg.marketplace.users.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
