package com.intuit.cg.marketplace.marketplace.controller;

import com.intuit.cg.marketplace.marketplace.entity.Project;
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
				linkTo(methodOn(ProjectBidsController.class).getBidsOnProject(project.getId())).withRel("bids"));
	}
}