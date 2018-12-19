package com.intuit.cg.backendtechassessment.model;

import com.intuit.cg.backendtechassessment.model.shared.DataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "Bids")
public class Bid extends DataType {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long buyerId;

    @NotNull
    @Positive
    private BigDecimal amount;

    //This value gets set from the api id value e.g. projects/5/bids since we post bids from the project root
    private Long projectId;
}
