package com.intuit.cg.marketplace.shared.controller;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class ResourceController {
    protected boolean resourceExists(JpaRepository<?, Long> repository, Long id) {
        return repository.findById(id).isPresent();
    }
}
