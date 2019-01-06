package com.intuit.cg.marketplace.controllers.controller;

import com.intuit.cg.marketplace.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.marketplace.controllers.entity.Bid;
import com.intuit.cg.marketplace.controllers.repository.BidRepository;
import com.intuit.cg.marketplace.shared.controller.ResourceController;
import com.intuit.cg.marketplace.shared.exceptions.ResourceNotFoundException;
import com.intuit.cg.marketplace.users.controller.BuyerResourceAssembler;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.BIDS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMappingTemplate(value = BIDS)
class BidController extends ResourceController {
    private final BidRepository bidRepository;
    private final BidResourceAssembler bidAssembler;

    BidController(BidRepository bidRepository, BidResourceAssembler bidAssembler) {
        this.bidRepository = bidRepository;
        this.bidAssembler = bidAssembler;
    }

    //In reality I don't think this method is necessary or correct to include.
    //Bids are by far the most frequent entity and frequently returning all of
    //them can lead to performance issues
    @GetMapping
    Resources<Resource<Bid>> getBids() {
        List<Resource<Bid>> bids = bidRepository.findAll().stream()
                .map(bidAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(bids,
                linkTo(methodOn(BidController.class).getBids()).withSelfRel());
    }

    @GetMapping("/{id}")
    Resource<Bid> getBid(@PathVariable Long id) {
        Bid bid = bidRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return bidAssembler.toResource(bid);
    }
}
