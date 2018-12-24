package com.intuit.cg.backendtechassessment.marketplace.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class AutoBid extends Bid {
    private boolean autobid;
    private BigDecimal maxBidAmount;
    private BigDecimal bidAdditionalAmount;
}
