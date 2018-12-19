package com.intuit.cg.backendtechassessment.model.shared;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public abstract class User extends DataType {
    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String email;

    public void updateInfoWith(User user) {
        this.firstName = user.firstName.isEmpty() ? this.firstName : user.firstName;
        this.lastName = user.lastName.isEmpty() ? this.lastName : user.lastName;
        this.email = user.email.isEmpty() ? this.email : user.email;
    }
}
