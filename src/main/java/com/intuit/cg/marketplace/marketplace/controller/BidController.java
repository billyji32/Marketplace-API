package com.intuit.cg.marketplace.marketplace.controller;

import com.intuit.cg.marketplace.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.marketplace.shared.controller.ResourceController;
import com.intuit.cg.marketplace.shared.exceptions.ResourceNotFoundException;
import com.intuit.cg.marketplace.marketplace.entity.Bid;
import com.intuit.cg.marketplace.marketplace.repository.BidRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.BIDS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMappingTemplate(value = BIDS)
class BidController extends ResourceController {
	private final BidRepository bidRepository;
	private final BidResourceAssembler assembler;

	BidController(BidRepository bidRepository, BidResourceAssembler assembler) {
		this.bidRepository = bidRepository;
		this.assembler = assembler;
	}

	//In reality I don't think this method is necessary or correct to include.
	//Bids are by far the most frequent entity and frequently returning all of
	//them can lead to performance issues
	@GetMapping
	Resources<Resource<Bid>> getBids() {
		List<Resource<Bid>> bids = bidRepository.findAll().stream()
				.map(assembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(bids,
				linkTo(methodOn(BidController.class).getBids()).withSelfRel());
	}

	@GetMapping("/{id}")
	Resource<Bid> getBid(@PathVariable Long id) {
		Bid bid = bidRepository.findById(id)
				.orElseThrow(ResourceNotFoundException::new);

		return assembler.toResource(bid);
	}
}
