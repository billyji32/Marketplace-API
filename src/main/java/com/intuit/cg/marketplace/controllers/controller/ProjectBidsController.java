package com.intuit.cg.marketplace.controllers.controller;

import com.intuit.cg.marketplace.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.marketplace.controllers.entity.Bid;
import com.intuit.cg.marketplace.controllers.entity.Project;
import com.intuit.cg.marketplace.controllers.repository.BidRepository;
import com.intuit.cg.marketplace.controllers.repository.ProjectRepository;
import com.intuit.cg.marketplace.shared.controller.ResourceController;
import com.intuit.cg.marketplace.shared.exceptions.InvalidBidException;
import com.intuit.cg.marketplace.shared.exceptions.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.PROJECTS;

//This controller exists due to how much bids and projects need to interact. Bids need to be checked against current values on projects,
//and so I decided to set the api for posting new bids as projects/id/bids. I think that you could/should flatten it to just post new bids to /bids
//so as to fit with the rest of the api design, but for the purposes of this demo this should be fine.
@RestController
@JsonRequestMappingTemplate(value = PROJECTS)
public class ProjectBidsController extends ResourceController {
    private final ProjectRepository projectRepository;
    private final BidRepository bidRepository;
    private final BidResourceAssembler bidAssembler;

    ProjectBidsController(ProjectRepository projectRepository,
                          BidRepository bidRepository, BidResourceAssembler bidAssembler) {
        this.projectRepository = projectRepository;
        this.bidRepository = bidRepository;
        this.bidAssembler = bidAssembler;
    }

    @GetMapping("/{id}/bids")
    Resources<Resource<Bid>> getBidsOnProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        List<Resource<Bid>> bids = project.getBids().stream()
                .map(bidAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(bids);
    }

    @GetMapping("/{id}/bids/lowest")
    Resource<Bid> getLowestBidOnProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return bidAssembler.toResource(project.getLowestBid());
    }

    //Return a ResponseEntity here due to possibility of good/bad request
    @PostMapping("/{id}/bids")
    ResponseEntity<Resource<Bid>> newBidOnProject(@RequestBody @Valid Bid bid, @PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (isValidBid(bid, project)) {
            project.addBid(bid);

            Resource<Bid> bidResource = bidAssembler.toResource(bidRepository.save(bid));
            return new ResponseEntity<>(bidResource, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private boolean isValidBid(Bid newBid, Project project) {
        return projectDeadlineNotPassed(project) &&
                bidAmountLessThanCurrentLowest(newBid, project) &&
                bidAmountLessThanBudget(newBid, project);
    }

    private boolean projectDeadlineNotPassed(Project project) {
        if (project.getDeadline().isAfter(LocalDateTime.now()))
            return true;
        else
            throw new InvalidBidException("Deadline on project has already passed and no bids are accepted");
    }

    //In reality this isn't desired since we might want to pay more for a better buyer
    //but I am including it to fulfill the project specifications
    private boolean bidAmountLessThanCurrentLowest(Bid newBid, Project project) {
        double curMinBid = project.getLowestBid().getAmount();
        if (curMinBid == 0.0 || newBid.getAmount() < curMinBid)
            return true;
        else
            throw new InvalidBidException("Your bid is not lower than the current lowest bid of " + curMinBid);
    }

    private boolean bidAmountLessThanBudget(Bid newBid, Project project) {
        double maxBudget = project.getBudget();
        if (maxBudget >= newBid.getAmount())
            return true;
        else
            throw new InvalidBidException("Bid is greater than the max budget of " + maxBudget);
    }
}
