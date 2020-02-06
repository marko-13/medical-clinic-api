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
@DiscriminatorValue("P")
public class Patient extends AppUser {

	@Column(name = "JMBG", nullable = true)
	private String JMBG;

	@OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
	private MedicalHistory medicalHistory;

	@ManyToMany(mappedBy = "patients")
	private List<Clinic> clinics;

	@OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Appointment> appointments;

}
