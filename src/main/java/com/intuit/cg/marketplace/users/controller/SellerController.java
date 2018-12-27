package com.intuit.cg.marketplace.users.controller;

import com.intuit.cg.marketplace.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.marketplace.controllers.controller.ProjectResourceAssembler;
import com.intuit.cg.marketplace.controllers.entity.Project;
import com.intuit.cg.marketplace.controllers.repository.ProjectRepository;
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
    private final SellerResourceAssembler sellerAssembler;
    private final ProjectResourceAssembler projectAssembler;

    SellerController(SellerRepository sRepository, ProjectRepository pRepository,
                     SellerResourceAssembler sAssembler, ProjectResourceAssembler pAssembler) {
        this.sellerRepository = sRepository;
        this.projectRepository = pRepository;
        this.sellerAssembler = sAssembler;
        this.projectAssembler = pAssembler;
    }

    @GetMapping
    Resources<Resource<Seller>> getSellers() {
        List<Resource<Seller>> sellers = sellerRepository.findAll().stream()
                .map(sellerAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(sellers,
                linkTo(methodOn(SellerController.class).getSellers()).withSelfRel());
    }

    @GetMapping("/{id}")
    Resource<Seller> getSeller(@PathVariable Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return sellerAssembler.toResource(seller);
    }

    @GetMapping("/{id}/projects")
    Resources<Resource<Project>> getProjectsBySeller(@PathVariable Long id) {
        if (resourceExists(sellerRepository, id)) {
            List<Resource<Project>> projects = projectRepository.findBySellerId(id).stream()
                    .map(projectAssembler::toResource)
                    .collect(Collectors.toList());

            return new Resources<>(projects);
        } else
            throw new ResourceNotFoundException();
    }

    @PostMapping
    ResponseEntity<Resource<Seller>> newSeller(@RequestBody @Valid Seller seller) {
        Resource<Seller> sellerResource = sellerAssembler.toResource(sellerRepository.save(seller));
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

        Resource<Seller> sellerResource = sellerAssembler.toResource(updatedSeller);

        return ResponseEntity
                .created(new URI(sellerResource.getId().expand().getHref()))
                .body(sellerResource);
    }
}
