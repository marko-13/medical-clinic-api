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
public class MedicalReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, updatable = false, nullable = false)
	private Long id;

	@Column(name = "exam_description", unique = false, nullable = false)
	private String examDescription;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(
			name = "MedicalReport_Diagnosis",
			joinColumns = {@JoinColumn(name = "mreport_id")},
			inverseJoinColumns = {@JoinColumn(name = "diagnosis_id")}
	)
	private List<DiagnosisRegistry> diagnosisRegistry;

	@OneToOne
	@MapsId
	private Examination examination;

	@OneToOne
	@MapsId
	@JoinColumn(nullable = true)
	private Prescription prescription;

	@ManyToOne
	@JoinColumn(name = "medical_history_id", nullable = false)
	private MedicalHistory medicalHistory;
}
