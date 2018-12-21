package com.intuit.cg.backendtechassessment.users.controller;

import com.intuit.cg.backendtechassessment.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.backendtechassessment.shared.controller.ResourceController;
import com.intuit.cg.backendtechassessment.shared.exceptions.ResourceNotFoundException;
import com.intuit.cg.backendtechassessment.marketplace.entity.Bid;
import com.intuit.cg.backendtechassessment.users.entity.Buyer;
import com.intuit.cg.backendtechassessment.marketplace.repository.BidRepository;
import com.intuit.cg.backendtechassessment.users.repository.BuyerRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static com.intuit.cg.backendtechassessment.configuration.requestmappings.RequestMappings.BUYERS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMappingTemplate(value = BUYERS)
@SuppressWarnings("unused")
public class BuyerController extends ResourceController {
	private final BuyerRepository buyerRepository;
	private final BidRepository bidRepository;
	private final BuyerResourceAssembler assembler;

	BuyerController(BuyerRepository buyerRepository, BidRepository bidRepository, BuyerResourceAssembler assembler) {
		this.buyerRepository = buyerRepository;
		this.bidRepository = bidRepository;
		this.assembler = assembler;
	}

	@GetMapping
	Resources<Resource<Buyer>> getBuyers() {
		List<Resource<Buyer>> buyers = buyerRepository.findAll().stream()
				.map(assembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(buyers,
				linkTo(methodOn(BuyerController.class).getBuyers()).withSelfRel());
	}

	@GetMapping("/{id}")
	public Resource<Buyer> getBuyer(@PathVariable Long id) {
		Buyer buyer = buyerRepository.findById(id)
				.orElseThrow(ResourceNotFoundException::new);

		return assembler.toResource(buyer);
	}

	@GetMapping("/{id}/bids")
	public List<Bid> getBids(@PathVariable Long id) {
		if(resourceExists(buyerRepository, id))
			return bidRepository.findByBuyerId(id);
		else
			throw new ResourceNotFoundException();
	}

	@PostMapping
	ResponseEntity<Resource<Buyer>> newBuyer(@RequestBody @Valid Buyer buyer) {
		Resource<Buyer> buyerResource = assembler.toResource(buyerRepository.save(buyer));
		return new ResponseEntity<>(buyerResource, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	ResponseEntity<Resource<Buyer>> replaceBuyer(@RequestBody Buyer newBuyer, @PathVariable Long id) throws URISyntaxException {
		Buyer updatedBuyer = buyerRepository.findById(id)
				.map(oldBuyer -> {
					oldBuyer.updateInfoWith(newBuyer);
					return buyerRepository.save(oldBuyer);
				})
				.orElseGet(() -> {
					newBuyer.setId(id);
					return buyerRepository.save(newBuyer);
				});

		Resource<Buyer> buyerResource = assembler.toResource(updatedBuyer);

		return ResponseEntity
				.created(new URI(buyerResource.getId().expand().getHref()))
				.body(buyerResource);
	}
}
