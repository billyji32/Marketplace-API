package com.intuit.cg.backendtechassessment.marketplace.controller;

import com.intuit.cg.backendtechassessment.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.backendtechassessment.shared.controller.ResourceController;
import com.intuit.cg.backendtechassessment.shared.exceptions.InvalidBidException;
import com.intuit.cg.backendtechassessment.shared.exceptions.ResourceNotFoundException;
import com.intuit.cg.backendtechassessment.marketplace.entity.Bid;
import com.intuit.cg.backendtechassessment.marketplace.entity.Project;
import com.intuit.cg.backendtechassessment.marketplace.repository.BidRepository;
import com.intuit.cg.backendtechassessment.marketplace.repository.ProjectRepository;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intuit.cg.backendtechassessment.configuration.requestmappings.RequestMappings.PROJECTS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMappingTemplate(value = PROJECTS)
@SuppressWarnings("unused")
public class ProjectController extends ResourceController {
	private final ProjectRepository projectRepository;
	private final BidRepository bidRepository;
	private final ProjectResourceAssembler projectAssembler;
	private final BidResourceAssembler bidAssembler;
	private final Map<Long, BigDecimal> highestBidCache = new HashMap<>();

	ProjectController(ProjectRepository projectRepository, BidRepository bidRepository,
					  ProjectResourceAssembler projectAssembler, BidResourceAssembler bidAssembler)
	{
		this.projectRepository = projectRepository;
		this.bidRepository = bidRepository;
		this.projectAssembler = projectAssembler;
		this.bidAssembler = bidAssembler;
	}

	@GetMapping
	Resources<Resource<Project>> getProjects() {
		List<Resource<Project>> projects = projectRepository.findAll().stream()
				.map(projectAssembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(projects,
				linkTo(methodOn(ProjectController.class).getProjects()).withSelfRel());
	}

	@GetMapping("/{id}")
	public Resource<Project> getProject(@PathVariable Long id) {
		Project project = projectRepository.findById(id)
				.orElseThrow(ResourceNotFoundException::new);

		return projectAssembler.toResource(project);
	}

	@GetMapping("/{id}/bids")
	public List<Bid> getBids(@PathVariable Long id) {
		if(resourceExists(projectRepository, id))
			return bidRepository.findByProjectId(id);
		else
			throw new ResourceNotFoundException();
	}

	@GetMapping("/{id}/bids/highest")
	public Bid getHighestBid(@PathVariable Long id) {
		if(resourceExists(projectRepository, id))
			return bidRepository.findTopByProjectIdOrderByAmountDesc(id);
		else
			throw new ResourceNotFoundException();
	}

	@PutMapping("/{id}")
	ResponseEntity<Resource<Project>> replaceProject(@RequestBody Project newProject, @PathVariable Long id) throws URISyntaxException {
		Project updatedProject = projectRepository.findById(id)
				.map(oldProject -> {
					oldProject.updateInfoWith(newProject);
					return projectRepository.save(oldProject);
				})
				.orElseGet(() -> {
					newProject.setId(id);
					return projectRepository.save(newProject);
				});

		Resource<Project> projectResource = projectAssembler.toResource(updatedProject);

		return ResponseEntity
				.created(new URI(projectResource.getId().expand().getHref()))
				.body(projectResource);
	}

	@PostMapping
	ResponseEntity<Resource<Project>> newProject(@RequestBody @Valid Project project) {
		Resource<Project> projectResource = projectAssembler.toResource(projectRepository.save(project));
		return new ResponseEntity<>(projectResource, HttpStatus.OK);
	}

	@PostMapping("/{id}/bids")
	ResponseEntity<Resource<Bid>> newBid(@RequestBody @Valid Bid bid, @PathVariable Long id) {
		if(isValidBid(bid, id)) {
			bid.setProjectId(id);
			Resource<Bid> bidResource = bidAssembler.toResource(bidRepository.save(bid));
			highestBidCache.put(id, bid.getAmount());
			return new ResponseEntity<>(bidResource, HttpStatus.OK);
		}
		else {
			throw new ResourceNotFoundException();
		}
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
		BigDecimal curMaxBid = highestBidCache.getOrDefault(projectId, BigDecimal.ZERO);
		if(newBid.getAmount().compareTo(curMaxBid) > 0)
			return true;
		else
			throw new InvalidBidException("Your bid is not higher than the current highest bid of " + curMaxBid.toString());
	}
}
