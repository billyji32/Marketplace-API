package com.intuit.cg.marketplace.controllers;

import com.intuit.cg.marketplace.controllers.controller.ProjectController;
import com.intuit.cg.marketplace.controllers.controller.ProjectResourceAssembler;
import com.intuit.cg.marketplace.controllers.entity.Project;
import com.intuit.cg.marketplace.controllers.repository.ProjectRepository;
import com.intuit.cg.marketplace.users.entity.Seller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.PROJECTS;
import static com.intuit.cg.marketplace.utils.EntityGenerator.newProject;
import static com.intuit.cg.marketplace.utils.EntityGenerator.newSeller;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProjectController.class)
public class ProjectControllerTests {
    private static final Long PROJECT_ID = 1L;
    private static final Long PROJECT_SELLER_ID = 1L;
    private static final String PROJECTS_BASE_PATH = "http://localhost/projects/";
    private static final String SELLERS_BASE_PATH = "http://localhost/sellers/";

    @Autowired
    private MockMvc projectMockMvc;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectResourceAssembler projectResourceAssembler;

    private Project project;

    @Before
    public void setup() {
        Seller seller = newSeller(PROJECT_SELLER_ID);
        project = newProject(PROJECT_ID, seller);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        when(projectResourceAssembler.toResource(any(Project.class))).thenCallRealMethod();
    }

    @Test
    public void testGetProjects() throws Exception {
        final ResultActions result = projectMockMvc.perform(get(PROJECTS));
        result.andExpect(status().isOk());
        verifyJson(result, "_embedded.projects[0].");
    }

    @Test
    public void testGetValidProjectWithDefaults() throws Exception {
        final ResultActions result = projectMockMvc.perform(get(PROJECTS + "/" + PROJECT_ID));
        result.andExpect(status().isOk());
        verifyJson(result, "");
    }

    @Test
    public void testGetValidProjectWithOverridenDefaults() throws Exception {
        project.setBudget(124.99);
        project.setDeadline(LocalDateTime.now().withNano(0).plusDays(6).plusHours(2).plusMinutes(10));
        final ResultActions result = projectMockMvc.perform(get(PROJECTS + "/" + PROJECT_ID));
        result.andExpect(status().isOk());
        verifyJson(result, "");
    }

    @Test
    public void testGetInvalidProject() throws Exception {
        int invalidProjectId = 2;
        projectMockMvc.perform(get(PROJECTS + "/" + invalidProjectId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPostValidJson() throws Exception {
        String validJson = "{\"name\":\"testProj\",\"description\":\"project for sale\", \"budget\":\"10000.00\"}";
        projectMockMvc.perform(post(PROJECTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void testPostInvalidJson() throws Exception {
        String invalidJson = "{\"name\":\"testProj\"}";
        projectMockMvc.perform(post(PROJECTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
                .andExpect(status().isBadRequest());
    }

    private void verifyJson(final ResultActions action, final String jsonPrefix) throws Exception {
        action
                .andExpect(jsonPath(jsonPrefix + "id", is(project.getId().intValue())))
                .andExpect(jsonPath(jsonPrefix + "name", is(project.getName())))
                .andExpect(jsonPath(jsonPrefix + "deadline", is(project.getDeadline().toString())))
                .andExpect(jsonPath(jsonPrefix + "description", is(project.getDescription())))
                .andExpect(jsonPath(jsonPrefix + "budget", is(project.getBudget())))
                .andExpect(jsonPath(jsonPrefix + "_links.self.href", is(PROJECTS_BASE_PATH + project.getId())))
                .andExpect(jsonPath(jsonPrefix + "_links.lowestBid.href", is(PROJECTS_BASE_PATH + project.getId() + "/bids/lowest")))
                .andExpect(jsonPath(jsonPrefix + "_links.bids.href", is(PROJECTS_BASE_PATH + project.getId() + "/bids")))
                .andExpect(jsonPath(jsonPrefix + "_links.seller.href", is(SELLERS_BASE_PATH + project.getId())));
    }
}
