package com.intuit.cg.backendtechassessment.controller.shared;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class ResourceController {
    protected boolean resourceExists(JpaRepository repository, Long id) {
        return repository.findById(id).isPresent();
    }
}
