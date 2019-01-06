package com.intuit.cg.marketplace.controllers.entity;

import com.intuit.cg.marketplace.shared.entity.DataType;
import com.intuit.cg.marketplace.users.entity.Buyer;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

//Bids will have the highest unique entry count and as such should be as lightweight as possible
//Should probably move non-winning bids to a separate cheaper database after the project deadline ends
//and just keep the winning bid as that will probably still be polled fairly often
@Entity
@Data
@Table(name = "Bids")
public class Bid extends DataType {
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    @Positive
    private double amount;
}
