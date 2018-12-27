package com.intuit.cg.marketplace.controllers.controller;

import com.intuit.cg.marketplace.configuration.requestmappings.JsonRequestMappingTemplate;
import com.intuit.cg.marketplace.controllers.entity.Project;
import com.intuit.cg.marketplace.controllers.repository.ProjectRepository;
import com.intuit.cg.marketplace.shared.controller.ResourceController;
import com.intuit.cg.marketplace.shared.exceptions.ResourceNotFoundException;
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

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.PROJECTS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@JsonRequestMappingTemplate(value = PROJECTS)
@SuppressWarnings("unused")
public class ProjectController extends ResourceController {
    private final ProjectRepository projectRepository;
    private final ProjectResourceAssembler projectAssembler;

    ProjectController(ProjectRepository projectRepository, ProjectResourceAssembler projectAssembler) {
        this.projectRepository = projectRepository;
        this.projectAssembler = projectAssembler;
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
    Resource<Project> getProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return projectAssembler.toResource(project);
    }

    @PostMapping
    ResponseEntity<Resource<Project>> newProject(@RequestBody @Valid Project project) {
        Resource<Project> projectResource = projectAssembler.toResource(projectRepository.save(project));
        return new ResponseEntity<>(projectResource, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<Resource<Project>> updateOrCreateNewProject(@RequestBody Project newProject, @PathVariable Long id) throws URISyntaxException {
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
}
