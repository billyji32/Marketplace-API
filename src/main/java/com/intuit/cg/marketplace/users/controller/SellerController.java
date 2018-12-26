package com.intuit.cg.marketplace.users.controller;

import com.intuit.cg.marketplace.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.marketplace.projects.entity.Project;
import com.intuit.cg.marketplace.projects.repository.ProjectRepository;
import com.intuit.cg.marketplace.shared.controller.ResourceController;
import com.intuit.cg.marketplace.shared.exceptions.ResourceNotFoundException;
import com.intuit.cg.marketplace.users.entity.Seller;
import com.intuit.cg.marketplace.users.repository.SellerRepository;
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

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.SELLERS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMappingTemplate(value = SELLERS)
@SuppressWarnings("unused")
class SellerController extends ResourceController {
	private final SellerRepository sellerRepository;
	private final ProjectRepository projectRepository;
	private final SellerResourceAssembler assembler;

	SellerController(SellerRepository sRepository, ProjectRepository pRepository, SellerResourceAssembler assembler) {
		this.sellerRepository = sRepository;
		this.projectRepository = pRepository;
		this.assembler = assembler;
	}

	@GetMapping
	Resources<Resource<Seller>> getSellers() {
		List<Resource<Seller>> sellers = sellerRepository.findAll().stream()
				.map(assembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(sellers,
				linkTo(methodOn(SellerController.class).getSellers()).withSelfRel());
	}

	@GetMapping("/{id}")
	Resource<Seller> getSeller(@PathVariable Long id) {
		Seller seller = sellerRepository.findById(id)
				.orElseThrow(ResourceNotFoundException::new);

		return assembler.toResource(seller);
	}

	@GetMapping("/{id}/projects")
	List<Project> getProjectsBySeller(@PathVariable Long id) {
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
	ResponseEntity<Resource<Seller>> updateOrCreateNewSeller(@RequestBody Seller newSeller, @PathVariable Long id) throws URISyntaxException {
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
