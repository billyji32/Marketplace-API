package com.intuit.cg.backendtechassessment.users.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "Sellers")
public class Seller extends User {
}