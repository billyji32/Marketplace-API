package com.intuit.cg.backendtechassessment.marketplace.controller;

import com.intuit.cg.backendtechassessment.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.backendtechassessment.shared.controller.ResourceController;
import com.intuit.cg.backendtechassessment.shared.exceptions.ResourceNotFoundException;
import com.intuit.cg.backendtechassessment.marketplace.entity.Bid;
import com.intuit.cg.backendtechassessment.marketplace.repository.BidRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.intuit.cg.backendtechassessment.configuration.requestmappings.RequestMappings.BIDS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMappingTemplate(value = BIDS)
public class BidController extends ResourceController {
	private final BidRepository bidRepository;
	private final BidResourceAssembler assembler;

	BidController(BidRepository bidRepository, BidResourceAssembler assembler) {
		this.bidRepository = bidRepository;
		this.assembler = assembler;
	}

	@GetMapping
	Resources<Resource<Bid>> getBids() {
		List<Resource<Bid>> bids = bidRepository.findAll().stream()
				.map(assembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(bids,
				linkTo(methodOn(BidController.class).getBids()).withSelfRel());
	}

	@GetMapping("/{id}")
	public Resource<Bid> getBid(@PathVariable Long id) {
		Bid bid = bidRepository.findById(id)
				.orElseThrow(ResourceNotFoundException::new);

		return assembler.toResource(bid);
	}

}
