package com.intuit.cg.marketplace.controllers.controller;

import com.intuit.cg.marketplace.controllers.entity.Project;
import com.intuit.cg.marketplace.users.controller.SellerController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ProjectResourceAssembler implements ResourceAssembler<Project, Resource<Project>> {

    @Override
    public Resource<Project> toResource(Project project) {
        return new Resource<>(project,
                linkTo(methodOn(ProjectController.class).getProject(project.getId())).withSelfRel(),
                linkTo(methodOn(ProjectBidsController.class).getBidsOnProject(project.getId())).withRel("bids"),
                linkTo(methodOn(ProjectBidsController.class).getLowestBidOnProject(project.getId())).withRel("lowestBid"),
                linkTo(methodOn(SellerController.class).getSeller(project.getSeller().getId())).withRel("seller"));
    }
}