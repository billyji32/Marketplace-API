package com.intuit.cg.backendtechassessment.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//Use queries instead of unidirectional entity relationships
//Can use bidirectional entity relationships but bids -> projects
@Entity
@Data
@Table(name = "Projects")
public class Project {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
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
