package com.intuit.cg.backendtechassessment.repository;

import com.intuit.cg.backendtechassessment.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findBySellerId(Long sellerId);
}
