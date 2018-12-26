package com.intuit.cg.marketplace.projects.controller;

import com.intuit.cg.marketplace.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.marketplace.projects.entity.Bid;
import com.intuit.cg.marketplace.projects.repository.BidRepository;
import com.intuit.cg.marketplace.projects.repository.ProjectRepository;
import com.intuit.cg.marketplace.shared.controller.ResourceController;
import com.intuit.cg.marketplace.shared.exceptions.InvalidBidException;
import com.intuit.cg.marketplace.shared.exceptions.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
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
						  BidRepository bidRepository, BidResourceAssembler bidAssembler)
	{
		this.projectRepository = projectRepository;
		this.bidRepository = bidRepository;
		this.bidAssembler = bidAssembler;
	}

	@GetMapping("/{id}/bids")
	List<Resource<Bid>> getBidsOnProject(@PathVariable Long id) {
		if(resourceExists(projectRepository, id)) {
			return bidRepository.findByProjectId(id).stream()
					.map(bidAssembler::toResource)
					.collect(Collectors.toList());
		}
		else throw new ResourceNotFoundException();
	}

	@GetMapping("/{id}/bids/lowest")
	public Bid getLowestBidOnProject(@PathVariable Long id) {
		if(resourceExists(projectRepository, id)) {
			//Don't use the lowestBidCache here because we need the entire Bid object in the response
			return bidRepository.findTopByProjectIdOrderByAmountAsc(id);
		}
		else throw new ResourceNotFoundException();
	}

	@PostMapping("/{id}/bids")
	ResponseEntity<Resource<Bid>> newBidOnProject(@RequestBody @Valid Bid bid, @PathVariable Long id) {
		if (isValidBid(bid, id)) {
			bid.setProjectId(id);
			Resource<Bid> bidResource = bidAssembler.toResource(bidRepository.save(bid));
			lowestBidCache.put(id, bid.getAmount());
			return new ResponseEntity<>(bidResource, HttpStatus.OK);
		}
		else throw new ResourceNotFoundException();
	}

	private boolean isValidBid(Bid newBid, Long projectId) {
		return resourceExists(projectRepository, projectId) &&
				projectDeadlineNotPassed(projectId) &&
				bidAmountGreaterThanCurrentHighest(newBid, projectId);
	}

	private boolean projectDeadlineNotPassed(Long projectId) {
		if(projectRepository.findById(projectId).get().getDeadline().isAfter(LocalDateTime.now()))
			return true;
		else
			throw new InvalidBidException("Deadline on project has already passed and no bids are accepted");
	}

	private boolean bidAmountGreaterThanCurrentHighest(Bid newBid, Long projectId) {
		BigDecimal curMinBid = lowestBidCache.getOrDefault(projectId, BigDecimal.ZERO);
		if(curMinBid.equals(BigDecimal.ZERO) || newBid.getAmount().compareTo(curMinBid) < 0)
			return true;
		else
			throw new InvalidBidException("Your bid is not lower than the current lowest bid of " + curMinBid.toString());
	}
}
