package com.intuit.cg.marketplace.controllers.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.intuit.cg.marketplace.configuration.DeadlineSerializer;
import com.intuit.cg.marketplace.shared.entity.DataType;
import com.intuit.cg.marketplace.users.entity.Seller;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Projects")
//This exclusion is required to prevent a cyclic structure of projects <-> bids and projects <-> sellers,
//resulting in stackoverflow when running hashcode method to compare
@EqualsAndHashCode(exclude = {"bids", "seller"})
public class Project extends DataType {

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    @JsonSerialize(using = DeadlineSerializer.class)
    //If not specified, defaults to 2 weeks from date submitted
    private LocalDateTime deadline = LocalDateTime.now().withNano(0).plusWeeks(2);

    @NotNull
    @Positive
    private double budget;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    public void addBid(Bid bid) {
        bids.add(bid);
        bid.setProject(this);
    }

    //Because the latest bid added is always the lowest bid, we can just return it.
    //If no bids have been made, return a dummy bid with 0.0 as the amount
    public Bid getLowestBid() {
        if (!bids.isEmpty())
            return bids.get(bids.size() - 1);

        Bid dummyBid = new Bid();
        dummyBid.setAmount(0.0);
        return dummyBid;
    }

    public void updateInfoWith(Project project) {
        this.name = project.name.isEmpty() ? this.name : project.name;
        this.description = project.description.isEmpty() ? this.description : project.description;
        this.deadline = project.deadline != null && project.deadline.isAfter(LocalDateTime.now()) ? project.deadline : this.deadline;
        this.budget = project.budget < getLowestBid().getAmount() ? this.budget : project.budget;
    }
}
