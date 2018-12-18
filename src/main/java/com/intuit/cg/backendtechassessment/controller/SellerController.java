package com.intuit.cg.backendtechassessment.controller;

import com.intuit.cg.backendtechassessment.controller.assembler.SellerResourceAssembler;
import com.intuit.cg.backendtechassessment.controller.requestmappings.templates.JsonRequestMapping;
import com.intuit.cg.backendtechassessment.controller.shared.ResourceController;
import com.intuit.cg.backendtechassessment.exceptions.ResourceNotFoundException;
import com.intuit.cg.backendtechassessment.model.Project;
import com.intuit.cg.backendtechassessment.model.Seller;
import com.intuit.cg.backendtechassessment.repository.ProjectRepository;
import com.intuit.cg.backendtechassessment.repository.SellerRepository;
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

import static com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings.SELLERS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMapping(value = SELLERS)
@SuppressWarnings("unused")
public class SellerController extends ResourceController {
	private final SellerRepository sellerRepository;
	private final ProjectRepository projectRepository;
	private final SellerResourceAssembler assembler;

	SellerController(SellerRepository sRepository, ProjectRepository pRepository, SellerResourceAssembler assembler) {
		this.sellerRepository = sRepository;
		this.projectRepository = pRepository;
		this.assembler = assembler;
	}

	@GetMapping
	public Resources<Resource<Seller>> getSellers() {
		List<Resource<Seller>> sellers = sellerRepository.findAll().stream()
				.map(assembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(sellers,
				linkTo(methodOn(SellerController.class).getSellers()).withSelfRel());
	}

	@GetMapping("/{id}")
	public Resource<Seller> getSeller(@PathVariable Long id) {
		Seller seller = sellerRepository.findById(id)
				.orElseThrow(ResourceNotFoundException::new);

		return assembler.toResource(seller);
	}

	@GetMapping("/{id}/projects")
	public List<Project> getProjects(@PathVariable Long id) {
		if(resourceExists(sellerRepository, id))
			return projectRepository.findBySellerId(id);
		else
			throw new ResourceNotFoundException();
	}

	@PostMapping
	ResponseEntity<Resource<Seller>> newSeller(@RequestBody @Valid Seller seller) {
		Resource<Seller> sellerResource = assembler.toResource(sellerRepository.save(seller));
		return new ResponseEntity<>(sellerResource, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	ResponseEntity<Resource<Seller>> replaceSeller(@RequestBody Seller newSeller, @PathVariable Long id) throws URISyntaxException {
		Seller updatedSeller = sellerRepository.findById(id)
				.map(oldSeller -> {
					oldSeller.updateInfoWith(newSeller);
					return sellerRepository.save(oldSeller);
				})
				.orElseGet(() -> {
					newSeller.setId(id);
					return sellerRepository.save(newSeller);
				});

		Resource<Seller> sellerResource = assembler.toResource(updatedSeller);

		return ResponseEntity
				.created(new URI(sellerResource.getId().expand().getHref()))
				.body(sellerResource);
	}
}
