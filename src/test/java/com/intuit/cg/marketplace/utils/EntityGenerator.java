package com.intuit.cg.marketplace.utils;

import com.intuit.cg.marketplace.controllers.entity.Bid;
import com.intuit.cg.marketplace.controllers.entity.Project;
import com.intuit.cg.marketplace.users.entity.Buyer;
import com.intuit.cg.marketplace.users.entity.Seller;

import static com.intuit.cg.marketplace.utils.DefaultTestValues.DEFAULT_PROJECT_BUDGET;
import static com.intuit.cg.marketplace.utils.DefaultTestValues.DEFAULT_VALID_BID_AMOUNT;

public class EntityGenerator {
    private static final String EMAIL = "defaultEmail@gmail.com";
    private static final String FIRST_NAME = "bill";
    private static final String LAST_NAME = "ji";

    public static Seller newSeller(Long id) {
        Seller seller = new Seller();
        seller.setId(id);
        seller.setEmail(EMAIL);
        seller.setFirstName(FIRST_NAME);
        seller.setLastName(LAST_NAME);
        return seller;
    }

    public static Project newProject(Long id, Seller seller) {
        Project project = new Project();
        project.setId(id);
        project.setSeller(seller);
        project.setName("TestProject");
        project.setDescription("Project for testing");
        project.setBudget(DEFAULT_PROJECT_BUDGET);
        return project;
    }

    public static Bid newBid(Long id, Buyer buyer, Project project) {
        Bid bid = new Bid();
        bid.setId(id);
        bid.setBuyer(buyer);
        bid.setProject(project);
        bid.setAmount(DEFAULT_VALID_BID_AMOUNT);
        return bid;
    }

    public static Buyer newBuyer(Long id) {
        Buyer buyer = new Buyer();
        buyer.setId(id);
        buyer.setEmail(EMAIL);
        buyer.setFirstName(FIRST_NAME);
        buyer.setLastName(LAST_NAME);
        return buyer;
    }
}

