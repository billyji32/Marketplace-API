package com.intuit.cg.backendtechassessment.controller.assembler;

import com.intuit.cg.backendtechassessment.controller.ProjectController;
import com.intuit.cg.backendtechassessment.model.Project;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ProjectResourceAssembler implements ResourceAssembler<Project, Resource<Project>> {

	@Override
	public Resource<Project> toResource(Project project) {
		return new Resource<>(project,
				linkTo(methodOn(ProjectController.class).getProject(project.getId())).withSelfRel(),
				linkTo(methodOn(ProjectController.class).getBids(project.getId())).withRel("bids"));
	}
}