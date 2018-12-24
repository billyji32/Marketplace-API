package com.intuit.cg.marketplace.shared.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class DataType {
    //Specify GenerationType.IDENTITY because default is GenerationType.AUTO which creates unique values across all entities
    //rather than unique values per entity.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
