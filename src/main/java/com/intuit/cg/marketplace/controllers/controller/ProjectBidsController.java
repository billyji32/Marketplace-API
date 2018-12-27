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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.PROJECTS;

@RestController
@JsonRequestMappingTemplate(value = PROJECTS)
@SuppressWarnings("unused")
public class ProjectBidsController extends ResourceController {
    private final ProjectRepository projectRepository;
    private final BidRepository bidRepository;
    private final BidResourceAssembler bidAssembler;
    //Maps projectId to lowest bid on that project. Saves time during validation of new bids
    private final Map<Long, BigDecimal> lowestBidCache = new HashMap<>();

    ProjectBidsController(ProjectRepository projectRepository,
                          BidRepository bidRepository, BidResourceAssembler bidAssembler) {
        this.projectRepository = projectRepository;
        this.bidRepository = bidRepository;
        this.bidAssembler = bidAssembler;
    }

    @GetMapping("/{id}/bids")
    Resources<Resource<Bid>> getBidsOnProject(@PathVariable Long id) {
        if (resourceExists(projectRepository, id)) {
            List<Resource<Bid>> bids = bidRepository.findByProjectId(id).stream()
                    .map(bidAssembler::toResource)
                    .collect(Collectors.toList());

            return new Resources<>(bids);
        } else throw new ResourceNotFoundException();
    }

    @GetMapping("/{id}/bids/lowest")
    public ResponseEntity<Resource<Bid>> getLowestBidOnProject(@PathVariable Long id) {
        if (resourceExists(projectRepository, id)) {
            //Don't use the lowestBidCache here because we need the entire Bid object in the response
            Resource<Bid> bidResource = bidAssembler.toResource(bidRepository.findTopByProjectIdOrderByAmountAsc(id));
            return new ResponseEntity<>(bidResource, HttpStatus.OK);
        } else throw new ResourceNotFoundException();
    }

    @PostMapping("/{id}/bids")
    ResponseEntity<Resource<Bid>> newBidOnProject(@RequestBody @Valid Bid bid, @PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        if (isValidBid(bid, project)) {
            bid.setProjectId(id);
            Resource<Bid> bidResource = bidAssembler.toResource(bidRepository.save(bid));
            lowestBidCache.put(id, bid.getAmount());
            return new ResponseEntity<>(bidResource, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private boolean isValidBid(Bid newBid, Project project) {
        return projectDeadlineNotPassed(project) &&
                bidAmountGreaterThanCurrentHighest(newBid, project) &&
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
    private boolean bidAmountGreaterThanCurrentHighest(Bid newBid, Project project) {
        BigDecimal curMinBid = lowestBidCache.getOrDefault(project.getId(), BigDecimal.ZERO);
        if (curMinBid.equals(BigDecimal.ZERO) || newBid.getAmount().compareTo(curMinBid) < 0)
            return true;
        else
            throw new InvalidBidException("Your bid is not lower than the current lowest bid of " + curMinBid.toString());
    }

    private boolean bidAmountLessThanBudget(Bid newBid, Project project) {
        BigDecimal maxBudget = project.getBudget();
        if (project.getBudget().compareTo(newBid.getAmount()) > 0)
            return true;
        else
            throw new InvalidBidException("Bid is greater than the max budget of " + maxBudget.toString());
    }
}
