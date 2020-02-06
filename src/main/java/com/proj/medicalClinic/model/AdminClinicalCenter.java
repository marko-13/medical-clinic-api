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
@DiscriminatorValue("ACC")
public class AdminClinicalCenter extends AppUser {

    @ManyToOne
    @JoinColumn(name = "clinical_center_id", nullable = true)
    private ClinicalCenter clinicalCenter;
}
