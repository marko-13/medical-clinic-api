package com.proj.medicalClinic.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Data
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("AC")
public class AdminClinic extends AppUser {

    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = true)
    private Clinic clinic;
}
