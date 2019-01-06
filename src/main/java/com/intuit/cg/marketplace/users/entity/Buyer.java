package com.intuit.cg.marketplace.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intuit.cg.marketplace.controllers.entity.Bid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Data
@Table(name = "Buyers")
@EqualsAndHashCode(exclude = "bids")
public class Buyer extends User {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "buyer")
    @JsonIgnore
    private Set<Bid> bids;
}
