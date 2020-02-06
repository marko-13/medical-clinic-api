package com.proj.medicalClinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MedicalHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "height", unique = false, nullable = true)
	private double height;

	@Column(name = "weight", unique = false, nullable = true)
	private double weight;

	@Column(name = "blood_type", unique = false, nullable = true)
	@Enumerated(EnumType.STRING)
	private BloodType bloodType;

	@Column(name = "dioptre", unique = false, nullable = true)
	private double dioptre;

	@Column(name = "allergy", unique = false, nullable = true)
	private String allergy;

	@OneToMany(mappedBy = "medicalHistory", fetch = FetchType.LAZY, cascade =  CascadeType.ALL, orphanRemoval = true)
	private List<MedicalReport> diseaseHistory;

	@OneToOne
	@MapsId
	private Patient patient;
}
