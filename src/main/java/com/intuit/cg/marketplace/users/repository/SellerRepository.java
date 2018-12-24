package com.intuit.cg.marketplace.users.repository;

import com.intuit.cg.marketplace.users.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
