package com.intuit.cg.backendtechassessment.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "Bids")
public class Bid {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Long buyerId;

    @NotNull
    @Positive
    private BigDecimal amount;

    //This value gets set from the api id value e.g. projects/5/bids since we post bids from the project root
    private Long projectId;
}
