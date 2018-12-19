package com.intuit.cg.backendtechassessment.model;

import com.intuit.cg.backendtechassessment.model.shared.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "Sellers")
public class Seller extends User {
}