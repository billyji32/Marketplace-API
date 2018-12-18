package com.intuit.cg.backendtechassessment.repository;

import com.intuit.cg.backendtechassessment.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByBuyerId(Long buyerId);
    List<Bid> findByProjectId(Long projectId);
    Bid findTopByProjectIdOrderByAmountDesc(Long projectId);
}
