package com.intuit.cg.marketplace.controllers.repository;

import com.intuit.cg.marketplace.controllers.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
