package com.intuit.cg.marketplace.projects.controller;

import com.intuit.cg.marketplace.projects.entity.Bid;
import com.intuit.cg.marketplace.users.controller.BuyerController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class BidResourceAssembler implements ResourceAssembler<Bid, Resource<Bid>> {
	@Override
	public Resource<Bid> toResource(Bid bid) {
		return new Resource<>(bid,
				linkTo(methodOn(BidController.class).getBid(bid.getId())).withSelfRel(),
				linkTo(methodOn(BuyerController.class).getBuyer(bid.getBuyerId())).withRel("buyer"),
				linkTo(methodOn(ProjectController.class).getProject(bid.getProjectId())).withRel("project"));
	}
}