package com.intuit.cg.marketplace.controllers.repository;

import com.intuit.cg.marketplace.controllers.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByBuyerId(Long buyerId);

    List<Bid> findByProjectId(Long projectId);

    Bid findTopByProjectIdOrderByAmountAsc(Long projectId);
}
