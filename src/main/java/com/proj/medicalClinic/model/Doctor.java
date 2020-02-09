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
@DiscriminatorValue("DR")
public class Doctor extends AppUser{

	@Column(name = "shift", unique = false, nullable = true)
	private int shift;

	@Column(name = "review", unique = false, nullable = true)
	private double review;

	@Column(name = "review_count", unique = false, nullable = true)
	private int reviewCount;

	@ManyToOne
	@JoinColumn(name = "clinic_id", nullable = false)
	private Clinic clinic;

//	@OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<Examination> examinations;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "Doctors_Examinations",
			joinColumns = {@JoinColumn(name = "doctor_id")},
			inverseJoinColumns = {@JoinColumn(name = "examination_id")}
	)
	private List<Examination> examinations;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "Doctors_Operations",
			joinColumns = {@JoinColumn(name = "doctor_id")},
			inverseJoinColumns = {@JoinColumn(name = "operation_id")}
	)
	private List<Operation> operations;

	@ManyToMany
	@JoinTable(
			name = "Doctors_Services",
			joinColumns = {@JoinColumn(name = "doctor_id")},
			inverseJoinColumns = {@JoinColumn(name = "service_id")}
	)
	private List<Service> services;

	@OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Leave> leaves;

	//Mozda bi trebalo napraviti jos listu pacijenata koje je pregledao, a pacijent da ima listu doktora kod kojih je bio


}
