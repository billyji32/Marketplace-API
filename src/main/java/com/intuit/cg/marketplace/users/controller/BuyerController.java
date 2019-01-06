package com.intuit.cg.marketplace.users.controller;

import com.intuit.cg.marketplace.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.marketplace.controllers.controller.BidResourceAssembler;
import com.intuit.cg.marketplace.controllers.entity.Bid;
import com.intuit.cg.marketplace.controllers.repository.BidRepository;
import com.intuit.cg.marketplace.shared.controller.ResourceController;
import com.intuit.cg.marketplace.shared.exceptions.ResourceNotFoundException;
import com.intuit.cg.marketplace.users.entity.Buyer;
import com.intuit.cg.marketplace.users.repository.BuyerRepository;
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

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.BUYERS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMappingTemplate(value = BUYERS)
public class BuyerController extends ResourceController {
    private final BuyerRepository buyerRepository;
    private final BuyerResourceAssembler buyerAssembler;
    private final BidResourceAssembler bidAssembler;

    BuyerController(BuyerRepository buyerRepository, BuyerResourceAssembler buyerAssembler,
                    BidResourceAssembler bidAssembler) {
        this.buyerRepository = buyerRepository;
        this.buyerAssembler = buyerAssembler;
        this.bidAssembler = bidAssembler;
    }

    @GetMapping
    Resources<Resource<Buyer>> getBuyers() {
        List<Resource<Buyer>> buyers = buyerRepository.findAll().stream()
                .map(buyerAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(buyers,
                linkTo(methodOn(BuyerController.class).getBuyers()).withSelfRel());
    }

    @GetMapping("/{id}")
    public Resource<Buyer> getBuyer(@PathVariable Long id) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return buyerAssembler.toResource(buyer);
    }

    @GetMapping("/{id}/bids")
    Resources<Resource<Bid>> getBidsByBuyer(@PathVariable Long id) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        List<Resource<Bid>> bids = buyer.getBids().stream()
                    .map(bidAssembler::toResource)
                    .collect(Collectors.toList());
        return new Resources<>(bids);
    }

    @PostMapping
    ResponseEntity<Resource<Buyer>> newBuyer(@RequestBody @Valid Buyer buyer) {
        Resource<Buyer> buyerResource = buyerAssembler.toResource(buyerRepository.save(buyer));
        return new ResponseEntity<>(buyerResource, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<Resource<Buyer>> updateOrCreateNewBuyer(@RequestBody Buyer newBuyer, @PathVariable Long id) throws URISyntaxException {
        Buyer updatedBuyer = buyerRepository.findById(id)
                .map(oldBuyer -> {
                    oldBuyer.updateInfoWith(newBuyer);
                    return buyerRepository.save(oldBuyer);
                })
                .orElseGet(() -> {
                    newBuyer.setId(id);
                    return buyerRepository.save(newBuyer);
                });

        Resource<Buyer> buyerResource = buyerAssembler.toResource(updatedBuyer);

        return ResponseEntity
                .created(new URI(buyerResource.getId().expand().getHref()))
                .body(buyerResource);
    }
}
