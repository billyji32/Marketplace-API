package com.intuit.cg.backendtechassessment.controller;

import com.intuit.cg.backendtechassessment.controller.assembler.BidResourceAssembler;
import com.intuit.cg.backendtechassessment.controller.requestmappings.templates.JsonRequestMapping;
import com.intuit.cg.backendtechassessment.controller.shared.ResourceController;
import com.intuit.cg.backendtechassessment.exceptions.ResourceNotFoundException;
import com.intuit.cg.backendtechassessment.model.Bid;
import com.intuit.cg.backendtechassessment.repository.BidRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings.BIDS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMapping(value = BIDS)
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
