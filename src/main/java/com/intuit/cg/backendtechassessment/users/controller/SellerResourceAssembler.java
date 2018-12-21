package com.intuit.cg.backendtechassessment.users.controller;

import com.intuit.cg.backendtechassessment.users.entity.Seller;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class SellerResourceAssembler implements ResourceAssembler<Seller, Resource<Seller>> {

	@Override
	public Resource<Seller> toResource(Seller seller) {
		return new Resource<>(seller,
				linkTo(methodOn(SellerController.class).getSeller(seller.getId())).withSelfRel(),
				linkTo(methodOn(SellerController.class).getProjects(seller.getId())).withRel("projects"));
	}
}