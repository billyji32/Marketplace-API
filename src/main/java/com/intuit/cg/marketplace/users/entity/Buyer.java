package com.intuit.cg.marketplace.users.entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "Buyers")
public class Buyer extends User {
}
