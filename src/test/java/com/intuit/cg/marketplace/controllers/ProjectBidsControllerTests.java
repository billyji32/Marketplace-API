package com.intuit.cg.marketplace.controllers;

import com.intuit.cg.marketplace.controllers.controller.BidResourceAssembler;
import com.intuit.cg.marketplace.controllers.controller.ProjectBidsController;
import com.intuit.cg.marketplace.controllers.controller.ProjectResourceAssembler;
import com.intuit.cg.marketplace.controllers.entity.Bid;
import com.intuit.cg.marketplace.controllers.entity.Project;
import com.intuit.cg.marketplace.controllers.repository.BidRepository;
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
@WebMvcTest(ProjectBidsController.class)
public class ProjectBidsControllerTests {
    private static final Long PROJECT_ID = 1L;
    private static final Long BID_ID = 1L;
    private static final Long PROJECT_SELLER_ID = 1L;
    private static final Long BUYER_ID = 1L;

    private static final String BIDS_BASE_PATH = "http://localhost/bids/";
    private static final String BUYERS_BASE_PATH = "http://localhost/buyers/";
    private static final String PROJECTS_BASE_PATH = "http://localhost/projects/";

    private static final BigDecimal PRECISION = new BigDecimal(0.00001);

    @Autowired
    private MockMvc projectMockMvc;

    @MockBean
    private ProjectRepository projectRepository;
    @MockBean
    private BidRepository bidRepository;

    @MockBean
    private ProjectResourceAssembler projectResourceAssembler;
    @MockBean
    private BidResourceAssembler bidResourceAssembler;

    private Bid bid;
    private Project project;

    @Before
    public void setup() {
        project = new Project();
        project.setId(PROJECT_ID);
        project.setName("TestProject");
        project.setDescription("Project for testing");
        project.setSellerId(PROJECT_SELLER_ID);
        project.setBudget(10000.00);

        bid = new Bid();
        bid.setId(BID_ID);
        bid.setAmount(7000.0);
        bid.setBuyerId(BUYER_ID);
        bid.setProjectId(PROJECT_ID);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        when(projectResourceAssembler.toResource(any(Project.class))).thenCallRealMethod();

        when(bidRepository.findByProjectId(PROJECT_ID)).thenReturn(Collections.singletonList(bid));
        when(bidRepository.findTopByProjectIdOrderByAmountAsc(PROJECT_ID)).thenReturn(bid);
        when(bidResourceAssembler.toResource(any(Bid.class))).thenCallRealMethod();
    }

    @Test
    public void testGetBidsByProjectId() throws Exception {
        final ResultActions result = projectMockMvc.perform(get(PROJECTS + "/" + PROJECT_ID + "/bids"));
        result.andExpect(status().isOk());
        verifyJson(result, "_embedded.bidList[0].");
    }

    @Test
    public void testGetLowestBidByProjectId() throws Exception {
        final ResultActions result = projectMockMvc.perform(get(PROJECTS + "/" + PROJECT_ID + "/bids/lowest"));
        result.andExpect(status().isOk());
        verifyJson(result, "");
    }

    @Test
    public void testGetInvalidBidsByProjectId() throws Exception {
        int invalidProjectId = 2;
        projectMockMvc.perform(get(PROJECTS + "/" + invalidProjectId + "/bids"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPostWithValidAmount() throws Exception {
        String validJsonWithAmountEqualsToBudget = "{\"amount\":\"10000\",\"buyerId\":\"1\"}";
        projectMockMvc.perform(post(PROJECTS + "/" + PROJECT_ID + "/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJsonWithAmountEqualsToBudget)
        )
                .andExpect(status().isOk());

        String validJsonWithAmountLessThanBudget = "{\"amount\":\"1000\",\"buyerId\":\"1\"}";
        projectMockMvc.perform(post(PROJECTS + "/" + PROJECT_ID + "/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJsonWithAmountLessThanBudget)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void testInvalidPostWithAmountOverBudget() throws Exception {
        String invalidJson = "{\"amount\":\"100000\",\"buyerId\":\"1\"}";
        projectMockMvc.perform(post(PROJECTS + "/" + PROJECT_ID + "/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidPostOnClosedProject() throws Exception {
        String validJson = "{\"amount\":\"1000\",\"buyerId\":\"1\"}";
        project.setDeadline(LocalDateTime.now());
        projectMockMvc.perform(post(PROJECTS + "/" + PROJECT_ID + "/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
                .andExpect(status().isBadRequest());
    }

    private void verifyJson(final ResultActions action, final String jsonPrefix) throws Exception {
        action
                .andExpect(jsonPath(jsonPrefix + "id", is(bid.getId().intValue())))
                .andExpect(jsonPath(jsonPrefix + "buyerId", is(bid.getBuyerId().intValue())))
                .andExpect(jsonPath(jsonPrefix + "amount", is(bid.getAmount())))
                .andExpect(jsonPath(jsonPrefix + "_links.self.href", is(BIDS_BASE_PATH + bid.getId())))
                .andExpect(jsonPath(jsonPrefix + "_links.buyer.href", is(BUYERS_BASE_PATH + bid.getBuyerId())))
                .andExpect(jsonPath(jsonPrefix + "_links.project.href", is(PROJECTS_BASE_PATH + bid.getProjectId())));
    }
}
