package com.intuit.cg.marketplace.users.controller;

import com.intuit.cg.marketplace.users.entity.Buyer;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class BuyerResourceAssembler implements ResourceAssembler<Buyer, Resource<Buyer>> {

	@Override
	public Resource<Buyer> toResource(Buyer buyer) {
		return new Resource<>(buyer,
				linkTo(methodOn(BuyerController.class).getBuyer(buyer.getId())).withSelfRel(),
				linkTo(methodOn(BuyerController.class).getBidsByBuyer(buyer.getId())).withRel("bids"));
	}
}
