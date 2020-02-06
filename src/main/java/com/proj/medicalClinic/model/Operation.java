package com.proj.medicalClinic.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("OP")
public class Operation extends Appointment {

    @ManyToMany(mappedBy = "operations", fetch = FetchType.LAZY)
    private List<Doctor> doctors;

    @Column(name = "held", unique = false, nullable = false)
    private boolean held;

    @Column(name = "fast", unique = false, nullable = false)
    private boolean fast;
}
