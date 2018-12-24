package com.intuit.cg.backendtechassessment.marketplace.repository;

import com.intuit.cg.backendtechassessment.marketplace.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findBySellerId(Long sellerId);
    List<Project> deleteByDeadlineBefore(LocalDateTime curDate);
}
