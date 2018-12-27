package com.intuit.cg.marketplace.controllers;

import com.intuit.cg.marketplace.controllers.controller.ProjectController;
import com.intuit.cg.marketplace.controllers.controller.ProjectResourceAssembler;
import com.intuit.cg.marketplace.controllers.entity.Project;
import com.intuit.cg.marketplace.controllers.repository.ProjectRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.intuit.cg.marketplace.configuration.requestmappings.RequestMappings.PROJECTS;
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
    private static final String BASE_PATH = "http://localhost/projects/";

    @Autowired
    private MockMvc projectMockMvc;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectResourceAssembler projectResourceAssembler;

    private Project project;

    @Before
    public void setup() {
        project = new Project();
        project.setId(PROJECT_ID);
        project.setName("TestProject");
        project.setDescription("Project for testing");
        project.setSellerId(PROJECT_SELLER_ID);
        project.setBudget(10000.0);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        when(projectResourceAssembler.toResource(any(Project.class))).thenCallRealMethod();
    }

    @Test
    public void testGetProjects() throws Exception {
        final ResultActions result = projectMockMvc.perform(get(PROJECTS));
        result.andExpect(status().isOk());
        verifyJson(result, "_embedded.projectList[0].");
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
        String validJson = "{\"name\":\"testProj\",\"description\":\"project for sale\", \"sellerId\":\"1\", \"budget\":\"10000.00\"}";
        projectMockMvc.perform(post(PROJECTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void testPostInvalidJson() throws Exception {
        String invalidJson = "{\"name\":\"testProj\", \"sellerId\":\"1\"}";
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
                .andExpect(jsonPath(jsonPrefix + "sellerId", is(project.getSellerId().intValue())))
                .andExpect(jsonPath(jsonPrefix + "budget", is(project.getBudget())))
                .andExpect(jsonPath(jsonPrefix + "_links.self.href", is(BASE_PATH + project.getId())))
                .andExpect(jsonPath(jsonPrefix + "_links.bids.href", is(BASE_PATH + project.getId() + "/bids")));
    }
}
