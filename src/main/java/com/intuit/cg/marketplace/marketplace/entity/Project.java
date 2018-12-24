package com.intuit.cg.marketplace.marketplace.entity;

import com.intuit.cg.marketplace.shared.entity.DataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
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

    @NotEmpty
    private String description;

    @NotNull
    private Long sellerId;

    @NotNull
    //If not specified, defaults to 2 weeks from date submitted
    private LocalDateTime deadline = LocalDateTime.now().plusWeeks(2);

    @Positive
    private BigDecimal buyItNowPrice;

    public void updateInfoWith(Project project) {
        this.name = project.name.isEmpty() ? this.name : project.name;
        this.description = project.description.isEmpty() ? this.description : project.description;
        this.deadline = project.deadline != null && project.deadline.isAfter(LocalDateTime.now()) ? project.deadline : this.deadline;
    }
}
