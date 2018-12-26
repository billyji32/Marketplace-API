package com.intuit.cg.marketplace.projects.entity;

import com.intuit.cg.marketplace.shared.entity.DataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

//Bids will have the highest unique entry count and as such should be as lightweight as possible
//Should probably move non-winning bids to a separate cheaper database after the project deadline ends
//and just keep the winning bid as that will probably still be polled fairly often
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "Bids")
public class Bid extends DataType {
    @NotNull
    private Long buyerId;

    @NotNull
    @Positive
    //Use a BigDecimal here to prevent precision/rounding errors
    private BigDecimal amount;

    //This value gets set from the api ${id} value e.g. projects/${id}/bids since we post bids from the project root
    private Long projectId;
}
