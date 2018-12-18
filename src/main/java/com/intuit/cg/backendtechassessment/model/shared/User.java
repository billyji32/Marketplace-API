package com.intuit.cg.backendtechassessment.model.shared;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@MappedSuperclass
public abstract class User {
    //Specify GenerationType.IDENTITY because default is GenerationType.AUTO which creates unique values across all entities
    //rather than unique values per entity.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String email;

    public void updateInfoWith(User user) {
        this.firstName = user.firstName.isEmpty() ? this.firstName : user.firstName;
        this.lastName = user.lastName.isEmpty() ? this.lastName : user.lastName;
        this.email = user.email.isEmpty() ? this.email : user.email;
    }
}
