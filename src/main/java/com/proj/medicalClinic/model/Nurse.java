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
@DiscriminatorValue("NR")
public class Nurse extends AppUser{

	@OneToMany(mappedBy = "nurse", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Leave> leaves;

	@Column(name = "shift", nullable = true)
	private int shift;

	@ManyToOne
	@JoinColumn(name = "clinic_id", nullable = false)
	private Clinic clinic;

	@OneToMany(mappedBy = "nurse", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Examination> examinations;

	@OneToMany(mappedBy = "nurse", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Prescription> prescriptions;
}
