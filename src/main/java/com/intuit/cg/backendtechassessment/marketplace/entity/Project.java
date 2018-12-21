package com.intuit.cg.backendtechassessment.marketplace.entity;

import com.intuit.cg.backendtechassessment.shared.entity.DataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

//Use queries instead of unidirectional entity relationships
//Can use bidirectional entity relationships but bids -> projects
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "Projects")
public class Project extends DataType {
    @NotEmpty
    private String name;

    @NotNull
    private Long sellerId;

    @NotNull
    //If not specified, defaults to 2 weeks from date submitted
    private LocalDateTime deadline = LocalDateTime.now().plusWeeks(2);

    private String description;

    public void updateInfoWith(Project project) {
        this.name = project.name.isEmpty() ? this.name : project.name;
        this.description = project.description.isEmpty() ? this.description : project.description;
        this.deadline = project.deadline != null && project.deadline.isAfter(LocalDateTime.now()) ? project.deadline : this.deadline;
    }
}
